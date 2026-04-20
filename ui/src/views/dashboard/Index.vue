<template>
  <div class="analytics-container" v-loading="loading">
    <!-- 顶部卡片第一行（3个，统一布局） -->
    <el-row :gutter="16" class="panel-group">
      <el-col :xs="24" :sm="12" :md="8" :lg="8" v-for="(card, index) in firstLinePanel" :key="index">
        <div class="card-panel" @click="handleCardClick(card.route)">
          <div class="card-panel-header">
            <span class="card-panel-text">{{ card.title }}</span>
            <el-tag :type="card.tagType" effect="light" size="small" class="card-tag">{{ card.tagText }}</el-tag>
          </div>
          <div class="card-panel-body">
            <div class="card-num-wrapper">
              <div class="card-panel-num">{{ card.value }}</div>
            </div>
            <div class="card-icon-wrapper" :style="{ backgroundColor: card.iconColor + '15' }">
              <el-icon :size="24" :color="card.iconColor">
                <component :is="card.icon"/>
              </el-icon>
            </div>
          </div>
          <div class="card-panel-footer">
            <span class="footer-label">{{ card.footerText }}</span>
            <span class="footer-val" :title="card.footerValue">{{ card.footerValue }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 顶部卡片第二行（4个，新增待处理工单，统一布局） -->
    <el-row :gutter="16" class="panel-group">
      <el-col :xs="24" :sm="12" :md="6" :lg="6" v-for="(card, index) in secondLinePanel" :key="index">
        <div class="card-panel" @click="handleCardClick(card.route)">
          <div class="card-panel-header">
            <span class="card-panel-text">{{ card.title }}</span>
            <el-tag :type="card.tagType" effect="light" size="small" class="card-tag">{{ card.tagText }}</el-tag>
          </div>
          <div class="card-panel-body">
            <div class="card-num-wrapper">
              <div class="card-panel-num">{{ card.value }}</div>
            </div>
            <div class="card-icon-wrapper" :style="{ backgroundColor: card.iconColor + '15' }">
              <el-icon :size="24" :color="card.iconColor">
                <component :is="card.icon"/>
              </el-icon>
            </div>
          </div>
          <div class="card-panel-footer">
            <span class="footer-label">{{ card.footerText }}</span>
            <span class="footer-val" :title="card.footerValue">{{ card.footerValue }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 业务动态走势（完全保留原有结构） -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="main-chart-card">
          <template #header>
            <div class="chart-header">
              <div class="tabs">
                <span class="tab active">近30日工单和意向业务动态走势</span>
              </div>
            </div>
          </template>
          <div ref="lineChartRef" class="main-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第一行：企业统计（完全保留原有结构） -->
    <el-row :gutter="16" class="bottom-chart-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">入驻企业行业分布</span></template>
          <div ref="industryChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">入驻企业状态分布</span></template>
          <div ref="companyStatusChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：工单统计（完全保留原有结构） -->
    <el-row :gutter="16" class="bottom-chart-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">园区工单类型分布</span></template>
          <div ref="workTypeChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">园区工单状态分布</span></template>
          <div ref="workStatusChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行：留言统计（完全保留原有结构） -->
    <el-row :gutter="16" class="bottom-chart-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">意向留言类型分布</span></template>
          <div ref="msgTypeChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">意向留言状态分布</span></template>
          <div ref="msgStatusChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第四行：通知公告统计（完全保留原有结构） -->
    <el-row :gutter="16" class="bottom-chart-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">通知公告类型分布</span></template>
          <div ref="noticeTypeChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span class="chart-card-title">通知公告状态分布</span></template>
          <div ref="noticeStatusChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, markRaw, computed} from 'vue'
import {useRouter} from 'vue-router'
import * as echarts from 'echarts'
import {
  User, OfficeBuilding, Document, ChatDotSquare, Bell, Tools, CircleCheck, Edit, Check
} from '@element-plus/icons-vue'

// 引入你的 API（完全保留原有，无修改）
import enterpriseApi from '@/api/enterprise'
import inquiryApi from '@/api/inquiry'
import userApi from '@/api/user'
import workOrderApi from '@/api/workOrder'
import noticeApi from '@/api/notice'

// 路由跳转
const router = useRouter()

const loading = ref(false)

// --- 卡片基础数据（完全保留原有，仅新增待处理工单项） ---
const basePanelData = ref([
  {
    title: '园区用户', tagText: '总计', tagType: 'success',
    value: '0', footerText: '角色分布', footerValue: '加载中...',
    icon: User, iconColor: '#67c23a', route: '/system/user/list'
  },
  {
    title: '入驻企业', tagText: '总计', tagType: 'primary',
    value: '0', footerText: '当月新增/迁出', footerValue: '加载中...',
    icon: OfficeBuilding, iconColor: '#409eff', route: '/business/enterprise/list'
  },
  {
    title: '通知公告/政策/动态', tagText: '总计', tagType: 'info',
    value: '0', footerText: '当日/当月新增', footerValue: '加载中...',
    icon: Bell, iconColor: '#909399', route: '/business/notice/list'
  },
  {
    title: '入驻（迁出）申请', tagText: '待审核', tagType: 'danger',
    value: '0', footerText: '需及时处理', footerValue: '0',
    icon: Document, iconColor: '#f56c6c', route: '/business/enterprise/list'
  },
  {
    title: '意向留言', tagText: '待跟进', tagType: 'warning',
    value: '0', footerText: '招商线索', footerValue: '0',
    icon: ChatDotSquare, iconColor: '#e6a23c', route: '/business/inquiry/list'
  },
  {
    title: '投诉工单', tagText: '待受理', tagType: 'warning',
    value: '0', footerText: '需及时受理', footerValue: '0',
    icon: Check, iconColor: '#ff7d00', route: '/business/workorder/list'
  },
  {
    title: '投诉工单', tagText: '待处理', tagType: 'primary',
    value: '0', footerText: '处理中', footerValue: '0',
    icon: Edit, iconColor: '#409eff', route: '/business/workorder/list'
  }
])

// 分两行展示，第一行3个，第二行4个
const firstLinePanel = computed(() => basePanelData.value.slice(0, 3))
const secondLinePanel = computed(() => basePanelData.value.slice(3, 7))

// --- 图表 DOM 引用（完全保留原有，无修改） ---
const lineChartRef = ref(null)
const industryChartRef = ref(null)
const companyStatusChartRef = ref(null)
const workTypeChartRef = ref(null)
const workStatusChartRef = ref(null)
const msgTypeChartRef = ref(null)
const msgStatusChartRef = ref(null)
const noticeTypeChartRef = ref(null)
const noticeStatusChartRef = ref(null)
let chartInstances = []

// --- 公告映射配置（完全保留原有，无修改） ---
const noticeTypeMap = {
  1: {label: '政策推送', type: 'primary'},
  2: {label: '园区动态', type: 'success'},
  3: {label: '通知公告', type: 'warning'},
  4: {label: '内部通报', type: 'danger'}
}
const noticeStatusMap = {
  0: {label: '草稿', type: 'info'},
  1: {label: '已发布', type: 'success'},
  2: {label: '已撤回', type: 'warning'},
  3: {label: '已归档', type: 'info'}
}

// --- 工单状态映射（和工单页面完全一致） ---
const workOrderStatusMap = {
  0: '待受理',
  1: '处理中',
  2: '已办结',
  3: '已评价'
}

// --- 卡片点击跳转（完全保留原有，无修改） ---
const handleCardClick = (route) => {
  if (route) router.push(route)
}

// --- 核心业务逻辑（完全保留原有，仅新增待处理工单统计） ---
const loadDashboardData = async () => {
  loading.value = true
  try {
    const [
      userRes,
      enterpriseRes,
      pendingEnterpriseRes,
      inquiryRes,
      workOrderRes,
      noticeRes
    ] = await Promise.allSettled([
      userApi.page({pageNum: 1, pageSize: 1000}),
      enterpriseApi.listAll(),
      enterpriseApi.getPendingCount(),
      inquiryApi.getAdminPage({pageNum: 1, pageSize: 1000}),
      workOrderApi.page({pageNum: 1, pageSize: 1000}),
      noticeApi.page({pageNum: 1, pageSize: 1000})
    ])

    // ================= 1. 园区用户统计（完全保留原有，无修改） =================
    let totalUsers = 0;
    let roleStr = '暂无数据';
    if (userRes.status === 'fulfilled') {
      const userRecords = userRes.value.data.records || [];
      totalUsers = userRecords.length;
      const roleCountMap = {};
      userRecords.forEach(user => {
        const roleName = user.roleName || '暂无角色';
        roleCountMap[roleName] = (roleCountMap[roleName] || 0) + 1;
      });
      roleStr = Object.entries(roleCountMap)
          .sort((a, b) => b[1] - a[1])
          .map(([name, count]) => `${name}:${count}`)
          .join(' | ');
    }

    // ================= 2. 入驻企业统计（完全保留原有，无修改） =================
    let totalEnterprises = 0;
    let monthNewEnterprises = 0;
    let monthOutEnterprises = 0;
    const enterpriseRecords = enterpriseRes.status === 'fulfilled' ? (enterpriseRes.value.data || []) : [];
    if (enterpriseRes.status === 'fulfilled') {
      const now = new Date();
      const currentYear = now.getFullYear();
      const currentMonth = now.getMonth();
      const currentMonthStart = new Date(currentYear, currentMonth, 1);
      const currentMonthEnd = new Date(currentYear, currentMonth + 1, 0, 23, 59, 59, 999);

      totalEnterprises = enterpriseRecords.filter(e => e.status === 1).length;

      enterpriseRecords.forEach(e => {
        if (e.status === 1 && e.createTime) {
          const createTime = new Date(e.createTime);
          if (createTime >= currentMonthStart && createTime <= currentMonthEnd) {
            monthNewEnterprises++;
          }
        }
        if (e.status === 3 && e.updateTime) {
          const updateTime = new Date(e.updateTime);
          if (updateTime >= currentMonthStart && updateTime <= currentMonthEnd) {
            monthOutEnterprises++;
          }
        }
      });
    }

    // ================= 3. 通知公告统计（完全保留原有，无修改） =================
    let totalNotices = 0;
    let todayNewNotices = 0;
    let monthNewNotices = 0;
    const noticeRecords = noticeRes.status === 'fulfilled' ? (noticeRes.value.data.records || []) : [];
    if (noticeRes.status === 'fulfilled') {
      const now = new Date();
      const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const currentYear = now.getFullYear();
      const currentMonth = now.getMonth();
      const currentMonthStart = new Date(currentYear, currentMonth, 1);
      const currentMonthEnd = new Date(currentYear, currentMonth + 1, 0, 23, 59, 59, 999);

      totalNotices = noticeRecords.length;
      noticeRecords.forEach(item => {
        if (item.createTime) {
          const createTime = new Date(item.createTime);
          if (createTime >= todayStart) todayNewNotices++;
          if (createTime >= currentMonthStart && createTime <= currentMonthEnd) monthNewNotices++;
        }
      });
    }

    // ================= 4. 工单统计（完全保留原有，仅新增待处理工单统计） =================
    const workOrderRecords = workOrderRes.status === 'fulfilled' ? (workOrderRes.value.data.records || []) : [];
    const pendingWorkOrderCount = workOrderRecords.filter(item => item.status === 0).length;
    const processingWorkOrderCount = workOrderRecords.filter(item => item.status === 1).length;

    // ================= 5. 其他卡片数据（完全保留原有，无修改） =================
    const pendingEnterprises = pendingEnterpriseRes.status === 'fulfilled' ? (pendingEnterpriseRes.value.data || 0) : 0;
    const inquiryRecords = inquiryRes.status === 'fulfilled' ? (inquiryRes.value.data.records || []) : [];
    const totalInquiries = inquiryRes.status === 'fulfilled' ? (inquiryRes.value.data.total || inquiryRecords.length) : 0;
    const pendingInquiries = inquiryRecords.filter(item => item.status === 0).length;

    // ================= 6. 卡片赋值（完全保留原有，仅新增待处理工单赋值） =================
    basePanelData.value[0].value = totalUsers;
    basePanelData.value[0].footerValue = roleStr;
    basePanelData.value[1].value = totalEnterprises;
    basePanelData.value[1].footerValue = `新增:${monthNewEnterprises} | 迁出:${monthOutEnterprises}`;
    basePanelData.value[2].value = totalNotices;
    basePanelData.value[2].footerValue = `当日:${todayNewNotices} | 当月:${monthNewNotices}`;
    basePanelData.value[3].value = pendingEnterprises;
    basePanelData.value[3].footerValue = pendingEnterprises;
    basePanelData.value[4].value = pendingInquiries;
    basePanelData.value[4].footerValue = totalInquiries + ' 条总留言';
    basePanelData.value[5].value = pendingWorkOrderCount;
    basePanelData.value[5].footerValue = pendingWorkOrderCount;
    basePanelData.value[6].value = processingWorkOrderCount;
    basePanelData.value[6].footerValue = processingWorkOrderCount;

    // ================= 7. 近30天走势数据（完全保留原有，无修改） =================
    const dateList = [];
    const workOrderCountMap = {};
    const inquiryCountMap = {};
    const today = new Date();

    for (let i = 29; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      const dateStr = `${date.getMonth() + 1}/${date.getDate()}`;
      dateList.push(dateStr);
      workOrderCountMap[dateStr] = 0;
      inquiryCountMap[dateStr] = 0;
    }

    if (workOrderRes.status === 'fulfilled') {
      workOrderRecords.forEach(item => {
        if (item.createTime) {
          const createDate = new Date(item.createTime);
          const dateStr = `${createDate.getMonth() + 1}/${createDate.getDate()}`;
          if (workOrderCountMap.hasOwnProperty(dateStr)) workOrderCountMap[dateStr]++;
        }
      });
    }

    if (inquiryRes.status === 'fulfilled') {
      inquiryRecords.forEach(item => {
        if (item.createTime) {
          const createDate = new Date(item.createTime);
          const dateStr = `${createDate.getMonth() + 1}/${createDate.getDate()}`;
          if (inquiryCountMap.hasOwnProperty(dateStr)) inquiryCountMap[dateStr]++;
        }
      });
    }

    const workOrderData = dateList.map(date => workOrderCountMap[date]);
    const inquiryData = dateList.map(date => inquiryCountMap[date]);

    // ================= 8. 渲染所有图表（完全保留原有，无修改） =================
    renderCharts({
      enterprises: enterpriseRecords,
      inquiries: inquiryRecords,
      workOrders: workOrderRecords,
      notices: noticeRecords,
      lineChartData: {dateList, workOrderData, inquiryData}
    });

  } catch (error) {
    console.error("加载面板数据失败", error);
  } finally {
    loading.value = false;
  }
}

// --- 渲染图表逻辑（100%完全保留原有，无任何修改） ---
const renderCharts = (dataSets) => {
  chartInstances.forEach(chart => chart.dispose());
  chartInstances = [];

  // 1. 业务动态走势（完全保留）
  initLineChart(dataSets.lineChartData);

  // 2. 入驻企业行业分布（完全保留）
  const industryCount = dataSets.enterprises.reduce((acc, curr) => {
    const key = curr.industry || '其他行业';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const industryData = Object.keys(industryCount).map(key => ({name: key, value: industryCount[key]}));
  initIndustryChart(industryData.length ? industryData : [{name: '暂无数据', value: 0}]);

  // 3. 入驻企业状态分布（完全保留）
  const companyStatusMap = {0: '待审核', 1: '已入驻', 2: '已驳回', 3: '已迁出', 4: '迁出待审'};
  const companyStatusCount = dataSets.enterprises.reduce((acc, curr) => {
    const key = companyStatusMap[curr.status] || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const companyStatusData = Object.keys(companyStatusCount).map(key => ({name: key, value: companyStatusCount[key]}));
  initCompanyStatusChart(companyStatusData.length ? companyStatusData : [{name: '暂无数据', value: 0}]);

  // 4. 园区工单类型分布（完全保留）
  const workTypeMap = {1: '维修报备', 2: '业务咨询', 3: '投诉建议'};
  const workTypeCount = dataSets.workOrders.reduce((acc, curr) => {
    const key = workTypeMap[curr.type] || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const workTypeData = Object.keys(workTypeCount).map(key => ({name: key, value: workTypeCount[key]}));
  initWorkTypeChart(workTypeData.length ? workTypeData : [{name: '暂无数据', value: 0}]);

  // 5. 园区工单状态分布（完全保留）
  const workStatusMap = {0: '待受理', 1: '处理中', 2: '已办结', 3: '已评价'};
  const workStatusCount = dataSets.workOrders.reduce((acc, curr) => {
    const key = workStatusMap[curr.status] || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const workStatusData = Object.keys(workStatusCount).map(key => ({name: key, value: workStatusCount[key]}));
  initWorkStatusChart(workStatusData.length ? workStatusData : [{name: '暂无数据', value: 0}]);

  // 6. 意向留言类型分布（完全保留）
  const msgTypeMap = {1: '企业入驻', 2: '人才求职', 3: '配套服务', 4: '其他反馈'};
  const msgTypeCount = dataSets.inquiries.reduce((acc, curr) => {
    const key = msgTypeMap[curr.type] || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const msgTypeData = Object.keys(msgTypeCount).map(key => ({name: key, value: msgTypeCount[key]}));
  initMsgTypeChart(msgTypeData.length ? msgTypeData : [{name: '暂无数据', value: 0}]);

  // 7. 意向留言状态分布（完全保留）
  const msgStatusMap = {0: '待处理', 1: '跟进中', 2: '已转入驻', 3: '已完结', 4: '无效记录'};
  const msgStatusCount = dataSets.inquiries.reduce((acc, curr) => {
    const key = msgStatusMap[curr.status] || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const msgStatusData = Object.keys(msgStatusCount).map(key => ({name: key, value: msgStatusCount[key]}));
  initMsgStatusChart(msgStatusData.length ? msgStatusData : [{name: '暂无数据', value: 0}]);

  // 8. 通知公告类型分布（完全保留）
  const noticeTypeCount = dataSets.notices.reduce((acc, curr) => {
    const key = noticeTypeMap[curr.type]?.label || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const noticeTypeData = Object.keys(noticeTypeCount).map(key => ({name: key, value: noticeTypeCount[key]}));
  initNoticeTypeChart(noticeTypeData.length ? noticeTypeData : [{name: '暂无数据', value: 0}]);

  // 9. 通知公告状态分布（完全保留）
  const noticeStatusCount = dataSets.notices.reduce((acc, curr) => {
    const key = noticeStatusMap[curr.status]?.label || '其他';
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
  const noticeStatusData = Object.keys(noticeStatusCount).map(key => ({name: key, value: noticeStatusCount[key]}));
  initNoticeStatusChart(noticeStatusData.length ? noticeStatusData : [{name: '暂无数据', value: 0}]);
}

// --- 原有图表初始化方法（100%完全保留，无任何修改） ---
const initLineChart = (chartData) => {
  if (!lineChartRef.value) return;
  const chart = markRaw(echarts.init(lineChartRef.value));
  chartInstances.push(chart);
  const option = {
    tooltip: {trigger: 'axis', axisPointer: {type: 'line'}},
    legend: {data: ['新增工单', '新增意向'], bottom: '0%'},
    grid: {left: '2%', right: '2%', bottom: '10%', top: '10%', containLabel: true},
    xAxis: {
      type: 'category', boundaryGap: false,
      data: chartData.dateList,
      axisLine: {show: false}, axisTick: {show: false}, axisLabel: {color: '#999'}
    },
    yAxis: {type: 'value', splitLine: {lineStyle: {type: 'dashed', color: '#eee'}}, axisLabel: {color: '#999'}},
    series: [
      {
        name: '新增工单', type: 'line', smooth: true, symbol: 'circle',
        itemStyle: {color: '#10b981'},
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(16, 185, 129, 0.3)'
          }, {offset: 1, color: 'rgba(16, 185, 129, 0.05)'}])
        },
        data: chartData.workOrderData
      },
      {
        name: '新增意向', type: 'line', smooth: true, symbol: 'circle',
        itemStyle: {color: '#3b82f6'},
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(59, 130, 246, 0.3)'
          }, {offset: 1, color: 'rgba(59, 130, 246, 0.05)'}])
        },
        data: chartData.inquiryData
      }
    ]
  };
  chart.setOption(option);
  window.addEventListener('resize', () => chart.resize());
}

const initIndustryChart = (data) => {
  if (!industryChartRef.value) return;
  const chart = markRaw(echarts.init(industryChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}家 ({d}%)'},
    series: [{
      name: '行业', type: 'pie', radius: '60%',
      data: data,
      label: {show: true, formatter: '{b}: {c}家 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

const initCompanyStatusChart = (data) => {
  if (!companyStatusChartRef.value) return;
  const chart = markRaw(echarts.init(companyStatusChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}家 ({d}%)'},
    series: [{
      name: '状态', type: 'pie', radius: ['35%', '65%'],
      data: data,
      label: {show: true, formatter: '{b}: {c}家 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

const initWorkTypeChart = (data) => {
  if (!workTypeChartRef.value) return;
  const chart = markRaw(echarts.init(workTypeChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}单 ({d}%)'},
    series: [{
      name: '工单类型', type: 'pie', radius: '60%',
      data: data,
      label: {show: true, formatter: '{b}: {c}单 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

const initWorkStatusChart = (data) => {
  if (!workStatusChartRef.value) return;
  const chart = markRaw(echarts.init(workStatusChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}单 ({d}%)'},
    series: [{
      name: '工单状态', type: 'pie', radius: ['35%', '65%'],
      data: data,
      label: {show: true, formatter: '{b}: {c}单 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

const initMsgTypeChart = (data) => {
  if (!msgTypeChartRef.value) return;
  const chart = markRaw(echarts.init(msgTypeChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}条 ({d}%)'},
    series: [{
      name: '留言类型', type: 'pie', radius: '60%',
      data: data,
      label: {show: true, formatter: '{b}: {c}条 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

const initMsgStatusChart = (data) => {
  if (!msgStatusChartRef.value) return;
  const chart = markRaw(echarts.init(msgStatusChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}条 ({d}%)'},
    series: [{
      name: '留言状态', type: 'pie', radius: ['35%', '65%'],
      data: data,
      label: {show: true, formatter: '{b}: {c}条 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

// --- 通知公告类型饼图（完全保留原有，无修改） ---
const initNoticeTypeChart = (data) => {
  if (!noticeTypeChartRef.value) return;
  const chart = markRaw(echarts.init(noticeTypeChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}条 ({d}%)'},
    series: [{
      name: '公告类型', type: 'pie', radius: '60%',
      data: data,
      label: {show: true, formatter: '{b}: {c}条 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

// --- 通知公告状态环形图（完全保留原有，无修改） ---
const initNoticeStatusChart = (data) => {
  if (!noticeStatusChartRef.value) return;
  const chart = markRaw(echarts.init(noticeStatusChartRef.value));
  chartInstances.push(chart);
  chart.setOption({
    tooltip: {trigger: 'item', formatter: '{b}: {c}条 ({d}%)'},
    series: [{
      name: '公告状态', type: 'pie', radius: ['35%', '65%'],
      data: data,
      label: {show: true, formatter: '{b}: {c}条 ({d}%)'},
      labelLine: {show: true}
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

const handleResize = () => {
  chartInstances.forEach(chart => chart.resize())
}

onMounted(() => {
  loadDashboardData();
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstances.forEach(chart => chart.dispose())
})
</script>

<style scoped>
/* 全局容器：完全保留原有，无修改 */
.analytics-container {
  min-height: calc(100vh - 84px);
  box-sizing: border-box;
}

/* 顶部卡片组：间距优化，统一布局 */
.panel-group {
  margin-bottom: 16px;
}

/* 核心卡片：彻底修复高度不一致、文字溢出问题，所有卡片完全统一 */
.card-panel {
  background: #ffffff;
  border-radius: 10px;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 180px;
  max-height: 180px;
  box-sizing: border-box;
  box-shadow: 0 2px 14px 0 rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f1f5f9;
  cursor: pointer;
  overflow: hidden;
}

/* 卡片hover效果：完全保留原有，无修改 */
.card-panel:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px 0 rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

/* 卡片头部：固定高度，不被内容撑开 */
.card-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-shrink: 0;
  height: 20px;
  overflow: hidden;
}

.card-panel-text {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
  line-height: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

/* 标签样式：固定尺寸，不撑开布局 */
.card-tag {
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  flex-shrink: 0;
  white-space: nowrap;
}

/* 卡片主体：固定高度，完全统一 */
.card-panel-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-shrink: 0;
  height: 40px;
}

.card-num-wrapper {
  flex: 1;
  overflow: hidden;
}

/* 数字样式：固定尺寸，不溢出 */
.card-panel-num {
  font-size: 32px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1;
  letter-spacing: -0.5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 图标容器：固定尺寸，完全统一 */
.card-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
}

.card-panel:hover .card-icon-wrapper {
  transform: scale(1.05);
}

/* 卡片底部：彻底修复文字溢出，固定高度，最多1行，超出悬浮显示 */
.card-panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #64748b;
  padding-top: 12px;
  margin-top: auto;
  border-top: 1px solid #f1f5f9;
  line-height: 1.6;
  flex-shrink: 0;
  height: auto;
  gap: 8px;
  overflow: hidden;
}

.footer-label {
  flex-shrink: 0;
  font-weight: 400;
  white-space: nowrap;
}

/* 核心修复：文字最多1行，超出省略，不撑大卡片，title悬浮显示完整内容 */
.footer-val {
  text-align: right;
  word-break: keep-all;
  white-space: nowrap;
  max-width: 65%;
  font-weight: 500;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 主图表卡片：完全保留原有，无修改 */
.chart-row {
  margin-bottom: 16px;
}

.main-chart-card {
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 14px 0 rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

/* 图表头部：完全保留原有，无修改 */
:deep(.main-chart-card .el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 24px;
}

.chart-header .tabs {
  display: flex;
  gap: 20px;
}

.chart-header .tab {
  cursor: pointer;
  font-size: 15px;
  color: #64748b;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.chart-header .tab.active {
  color: #3b82f6;
  border-bottom: 2px solid #3b82f6;
  font-weight: 600;
}

.main-chart {
  height: 360px;
  width: 100%;
  padding: 0 8px;
  box-sizing: border-box;
}

/* 底部图表行：完全保留原有，无修改 */
.bottom-chart-row {
  margin-bottom: 16px;
}

/* 子图表卡片：完全保留原有，无修改 */
.sub-chart-card {
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 14px 0 rgba(0, 0, 0, 0.04);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sub-chart-card:hover {
  box-shadow: 0 8px 24px 0 rgba(0, 0, 0, 0.08);
}

/* 图表卡片标题：完全保留原有，无修改 */
.chart-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

:deep(.sub-chart-card .el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 24px;
  font-weight: 500;
}

.sub-chart {
  height: 320px;
  width: 100%;
  padding: 0 8px;
  box-sizing: border-box;
}

/* 响应式适配：完全保留原有，无修改 */
@media screen and (max-width: 1200px) {
  .analytics-container {
    padding: 16px;
  }

  .card-panel {
    padding: 16px 20px;
    height: 130px;
    max-height: 130px;
  }

  .card-panel-num {
    font-size: 28px;
  }

  .main-chart {
    height: 320px;
  }

  .sub-chart {
    height: 300px;
  }
}
</style>