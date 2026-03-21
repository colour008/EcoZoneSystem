package com.zone.common.enums;

import lombok.Getter;

/**
 * 经济开发区服务管理平台 - 业务状态码枚举
 * 编码规范：
 * - 通用码：0（成功）、-1（系统异常）
 * - 权限/用户相关：4001-4099
 * - 企业档案相关：4101-4199
 * - 服务工单相关：4201-4299
 * - 园区公告相关：4301-4399
 * - 角色菜单相关：4401-4499
 * - 参数校验相关：4801-4899
 */
@Getter
public enum ResponseCodeEnum {

	// ===================== 通用状态码 =====================
	SUCCESS(0, "操作成功"),
	SYSTEM_ERROR(-1, "系统异常，请稍后重试"),
	PARAM_ERROR(-2, "参数格式错误"),
	BUSINESS_ERROR(-3, "业务异常"),

	// ===================== 权限/用户相关（4001-4099） =====================
	USER_NOT_EXIST(4001, "用户不存在"),
	USER_STATUS_DISABLED(4002, "用户已被停用"),
	USER_PASSWORD_ERROR(4003, "账号或密码错误"),
	USER_NAME_DUPLICATE(4004, "账号已存在"),
	USER_PHONE_DUPLICATE(4005, "手机号已绑定其他账户"),
	USER_NOT_LOGIN(4006, "用户未登录，请先登录"),
	TOKEN_EXPIRED(4007, "登录已过期，请重新登录"),
	TOKEN_INVALID(4008, "令牌无效，请重新登录"),
	PERMISSION_DENIED(4009, "权限不足，无法操作"),


	// ===================== 企业档案相关（4101-4199） =====================
	ENTERPRISE_NOT_EXIST(4101, "企业档案不存在"),
	ENTERPRISE_NAME_DUPLICATE(4102, "企业名称已存在"),
	ENTERPRISE_CREDIT_CODE_DUPLICATE(4103, "统一社会信用代码已存在，请核实"),
	ENTERPRISE_STATUS_ERROR(4104, "企业状态异常，无法操作"),
	ENTERPRISE_USER_BIND_ERROR(4105, "该账号已绑定企业，请勿重复绑定"),

	// ===================== 服务工单相关（4201-4299） =====================
	WORK_ORDER_NOT_EXIST(4201, "服务工单不存在"),
	WORK_ORDER_STATUS_ERROR(4202, "工单状态异常，无法执行当前操作"),
	WORK_ORDER_ACCEPT_FAILED(4203, "工单已被受理，无法重复接单"),
	WORK_ORDER_COMPLETE_FAILED(4204, "工单未处理完成，无法办结"),
	WORK_ORDER_EVALUATE_FAILED(4205, "工单未办结，无法评价"),
	WORK_ORDER_REPEAT_SUBMIT(4206, "请勿重复提交相同诉求工单"),
	WORK_ORDER_HANDLER_ERROR(4207, "非当前处理人，无法操作"),

	// ===================== 园区公告相关（4301-4399） =====================
	NOTICE_NOT_EXIST(4301, "园区公告不存在"),
	NOTICE_PUBLISH_FAILED(4302, "公告已发布，无法修改"),
	NOTICE_STATUS_ERROR(4303, "公告状态异常，无法操作"),
	NOTICE_DELETE_FAILED(4304, "已发布公告无法删除，仅可下架"),

	// ===================== 角色菜单相关（4401-4499） =====================
	ROLE_NOT_EXIST(4401, "角色不存在"),
	ROLE_NAME_DUPLICATE(4402, "角色名称已存在"),
	ROLE_USED_CANNOT_DELETE(4403, "角色已分配用户，无法删除"),
	MENU_NOT_EXIST(4404, "菜单权限不存在"),
	MENU_PARENT_ERROR(4405, "上级菜单不存在"),
	MENU_USED_CANNOT_DELETE(4406, "菜单已分配角色，无法删除"),
	ROLE_ADD_FAILED(4407, "角色添加失败" ),
	ROLE_DELETE_FAILED(4408, "角色删除失败"),
	ROLE_UPDATE_FAILED(4409, "角色更新失败"),
	ROLE_CODE_EXIST(4410,"角色编码已存在" ),

	// ===================== 参数校验相关（4801-4899） =====================
	PARAM_EMPTY(4801, "必填参数不能为空"),
	PARAM_PHONE_ERROR(4802, "手机号格式错误"),
	PARAM_DATE_ERROR(4803, "日期格式错误"),
	PARAM_ID_ERROR(4804, "ID参数格式错误"),
	PARAM_LENGTH_ERROR(4805, "参数长度超出限制"), ;

	private final int bizCode;
	private final String msg;

	ResponseCodeEnum(int bizCode, String msg) {
		this.bizCode = bizCode;
		this.msg = msg;
	}

	/**
	 * 根据业务编码获取枚举对象
	 */
	public static ResponseCodeEnum getByBizCode(int bizCode) {
		for (ResponseCodeEnum enumItem : values()) {
			if (enumItem.getBizCode() == bizCode) {
				return enumItem;
			}
		}
		return SYSTEM_ERROR;
	}
}