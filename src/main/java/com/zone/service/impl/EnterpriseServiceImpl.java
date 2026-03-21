package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.EnterpriseDTO;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.entity.Enterprise;
import com.zone.domain.vo.EnterpriseVO;
import com.zone.mapper.EnterpriseMapper;
import com.zone.service.EnterpriseService;
import com.zone.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author: JamHoo
 * @Description: 企业服务实现类
 * @Date: 2026/3/21 14:22
 * @Version: 1.0
 */
@Service
@Slf4j
public class EnterpriseServiceImpl implements EnterpriseService {
	@Autowired
	private EnterpriseMapper enterpriseMapper;

	// ================== C端自助接口 ==================
	/**
	 * 提交入驻申请
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean apply(EnterpriseDTO enterpriseDTO) {

		// 1. 检测信用代码是否重复
		if (enterpriseMapper.countByCreditCode(enterpriseDTO.getCreditCode()) > 0) {
			throw new BusinessException(ResponseCodeEnum.ENTERPRISE_CREDIT_CODE_DUPLICATE);
		}

		// 2. 封装成 Entity
		Enterprise enterprise = new Enterprise();
		BeanUtils.copyProperties(enterpriseDTO, enterprise);

		// 3. 绑定当前操作用户，这样“李四”登录后提交，企业就自动记在“李四”名下了
		enterprise.setUserId(SecurityUtils.getUserId());

		// 4. 默认待审核
		enterprise.setStatus(0);
		log.info("用户 {} 提交入驻申请: {}", SecurityUtils.getUsername(), enterprise.getCompanyName());

		// 5. 插入数据库
		return enterpriseMapper.insert(enterprise) > 0;
	}

	/**
	 * 获取企业的入驻信息
	 *
	 * @return
	 */
	@Override
	public EnterpriseVO getMyEnterprise() {
		return null;
	}

	/**
	 * 修改入驻信息
	 *
	 * @param enterpriseDTO
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateMyEnterprise(EnterpriseDTO enterpriseDTO) {
		return false;
	}

	// ================== B端管控接口 ==================
	/**
	 * 获取所有企业列表
	 *
	 * @return
	 */
	@Override
	public List<EnterpriseVO> listAll() {
		// 1. 从 Mapper 获取 Entity 列表
		List<Enterprise> entities = enterpriseMapper.listAll();

		// 2. 转换成 VO 列表 (利用 BeanUtils)
		return entities.stream().map(entity -> {
			EnterpriseVO vo = new EnterpriseVO();
			BeanUtils.copyProperties(entity, vo);
			return vo;
		}).collect(Collectors.toList());
	}

	/**
	 * 获取企业分页列表
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public PageResult<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto) {
		// 1. 开启分页
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());

		// 2. 直接获取 VO 类型的 Page 对象
		// 此时 MyBatis 返回的就是 List<EnterpriseVO>，类型完全匹配
		Page<EnterpriseVO> page = enterpriseMapper.getEnterprisePage(dto);

		// 3. 直接封装返回
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	/**
	 * 审核入驻申请
	 *
	 * @param id     企业ID
	 * @param status 1:通过, 2:驳回
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean audit(Long id, Integer status) {
		// 1. 基本校验：状态值是否合法 (1-入驻, 2-驳回)
		if (status != 1 && status != 2) {
			throw new BusinessException(ResponseCodeEnum.PARAM_ERROR);
		}

		// 2. 权限校验：通常只有管理员 (ROLE_ADMIN) 或 专员 (ROLE_STAFF) 才能操作
		if (!SecurityUtils.isAdmin() && !SecurityUtils.getRoleCodes().contains("ROLE_STAFF")) {
			throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED);
		}
		log.info("管理员 {} 正在审核企业 ID: {}, 结果为: {}", SecurityUtils.getUsername(), id, status);

		// 3. 更新数据库
		int rows = enterpriseMapper.updateStatus(id, status);
		return rows > 0;
	}

	/**
	 * 获取企业详情
	 *
	 * @param id
	 * @return
	 */
	@Override
	public EnterpriseVO getDetailById(Long id) {
		return null;
	}

	/**
	 * 迁出企业
	 *
	 * @param id
	 * @return
	 */
	@Override
	public boolean moveOut(Long id) {
		return false;
	}

	/**
	 * 删除企业
	 *
	 * @param id
	 * @return
	 */
	@Override
	public boolean deleteById(Long id) {
		return false;
	}
}
