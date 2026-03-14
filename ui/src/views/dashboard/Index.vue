<template>
  <div class="analytics-container">
    <el-row :gutter="16" class="panel-group">
      <el-col :xs="24" :sm="12" :md="12" :lg="6" v-for="(card, index) in panelData" :key="index">
        <div class="card-panel">
          <div class="card-panel-header">
            <span class="card-panel-text">{{ card.title }}</span>
            <el-tag :type="card.tagType" effect="light" size="small">{{ card.tagText }}</el-tag>
          </div>
          <div class="card-panel-body">
            <div class="card-panel-num">{{ card.value }}</div>
            <img :src="card.icon" class="card-panel-icon" alt="icon"/>
          </div>
          <div class="card-panel-footer">
            <span>{{ card.footerText }}</span>
            <span>{{ card.footerValue }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="main-chart-card">
          <template #header>
            <div class="chart-header">
              <div class="tabs">
                <span class="tab active">流量趋势</span>
                <span class="tab">月访问量</span>
              </div>
            </div>
          </template>
          <div ref="lineChartRef" class="main-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="bottom-chart-row">
      <el-col :xs="24" :sm="24" :md="8">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span>访问数量</span></template>
          <div ref="radarChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span>访问来源</span></template>
          <div ref="pieChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8">
        <el-card shadow="never" class="sub-chart-card">
          <template #header><span>访问来源</span></template>
          <div ref="donutChartRef" class="sub-chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, markRaw} from 'vue'
import * as echarts from 'echarts'

// --- 顶部卡片模拟数据 ---
const panelData = ref([
  {
    title: '用户量', tagText: '日', tagType: 'primary',
    value: '2,000', footerText: '总用户量', footerValue: '120,000',
    icon: 'https://api.iconify.design/flat-color-icons:conference-call.svg' // 使用在线图标模拟
  },
  {
    title: '访问量', tagText: '周', tagType: 'success',
    value: '20,000', footerText: '总访问量', footerValue: '500,000',
    icon: 'https://api.iconify.design/flat-color-icons:combo-chart.svg'
  },
  {
    title: '下载量', tagText: '月', tagType: 'warning',
    value: '8,000', footerText: '总下载量', footerValue: '120,000',
    icon: 'https://api.iconify.design/flat-color-icons:down.svg'
  },
  {
    title: '使用量', tagText: '年', tagType: 'danger',
    value: '5,000', footerText: '总使用量', footerValue: '50,000',
    icon: 'https://api.iconify.design/flat-color-icons:approval.svg'
  }
])

// --- 图表 DOM 引用 ---
const lineChartRef = ref(null)
const radarChartRef = ref(null)
const pieChartRef = ref(null)
const donutChartRef = ref(null)

// 存储图表实例以便后续销毁和自适应
let chartInstances = []

// --- 初始化中间折线面积图 ---
const initLineChart = () => {
  const chart = markRaw(echarts.init(lineChartRef.value))
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {type: 'line'}
    },
    grid: {left: '2%', right: '2%', bottom: '0%', top: '5%', containLabel: true},
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['6:00', '8:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'],
      axisLine: {show: false},
      axisTick: {show: false},
      axisLabel: {color: '#999'}
    },
    yAxis: {
      type: 'value',
      splitLine: {lineStyle: {type: 'dashed', color: '#eee'}},
      axisLabel: {color: '#999'}
    },
    series: [
      {
        name: '流量',
        type: 'line',
        smooth: true,
        symbol: 'none',
        itemStyle: {color: '#3b82f6'},
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {offset: 0, color: 'rgba(59, 130, 246, 0.5)'},
            {offset: 1, color: 'rgba(59, 130, 246, 0.05)'}
          ])
        },
        data: [1000, 5000, 30000, 64000, 20000, 70000, 30000, 10000, 2000]
      },
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        symbol: 'none',
        itemStyle: {color: '#10b981'},
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {offset: 0, color: 'rgba(16, 185, 129, 0.5)'},
            {offset: 1, color: 'rgba(16, 185, 129, 0.05)'}
          ])
        },
        data: [500, 2000, 15000, 20000, 8000, 22000, 12000, 5000, 1000]
      }
    ]
  }
  chart.setOption(option)
  chartInstances.push(chart)
}

// --- 初始化底部雷达图 ---
const initRadarChart = () => {
  const chart = markRaw(echarts.init(radarChartRef.value))
  const option = {
    tooltip: {},
    radar: {
      radius: '65%',
      indicator: [
        {name: '引用', max: 10000},
        {name: '口碑', max: 10000},
        {name: '热度', max: 10000},
        {name: '贡献', max: 10000},
        {name: '产出', max: 10000}
      ],
      axisName: {color: '#666'}
    },
    series: [{
      type: 'radar',
      data: [
        {
          value: [4200, 3000, 20000, 35000, 50000],
          name: '移动端',
          itemStyle: {color: '#3b82f6'},
          areaStyle: {color: 'rgba(59, 130, 246, 0.4)'}
        },
        {
          value: [5000, 14000, 28000, 26000, 42000],
          name: 'PC端',
          itemStyle: {color: '#10b981'},
          areaStyle: {color: 'rgba(16, 185, 129, 0.4)'}
        }
      ]
    }]
  }
  chart.setOption(option)
  chartInstances.push(chart)
}

// --- 初始化底部饼图 ---
const initPieChart = () => {
  const chart = markRaw(echarts.init(pieChartRef.value))
  const option = {
    tooltip: {trigger: 'item'},
    legend: {bottom: '0%', left: 'center', icon: 'circle', itemWidth: 10},
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {borderRadius: 10, borderColor: '#fff', borderWidth: 2},
      label: {show: false, position: 'center'},
      emphasis: {label: {show: true, fontSize: 16, fontWeight: 'bold'}},
      labelLine: {show: false},
      data: [
        {value: 1048, name: '搜索引擎', itemStyle: {color: '#3b82f6'}},
        {value: 735, name: '直接访问', itemStyle: {color: '#10b981'}},
        {value: 580, name: '邮件营销', itemStyle: {color: '#f59e0b'}},
        {value: 484, name: '联盟广告', itemStyle: {color: '#ef4444'}}
      ]
    }]
  }
  chart.setOption(option)
  chartInstances.push(chart)
}

// --- 初始化底部环形图 ---
const initDonutChart = () => {
  const chart = markRaw(echarts.init(donutChartRef.value))
  const option = {
    tooltip: {trigger: 'item'},
    series: [{
      type: 'pie',
      radius: '70%',
      data: [
        {value: 300, name: '技术支持', itemStyle: {color: '#14b8a6'}},
        {value: 200, name: '外包', itemStyle: {color: '#0ea5e9'}},
        {value: 100, name: '其他', itemStyle: {color: '#8b5cf6'}}
      ],
      emphasis: {
        itemStyle: {shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)'}
      }
    }]
  }
  chart.setOption(option)
  chartInstances.push(chart)
}

// --- 窗口改变大小自适应 ---
const handleResize = () => {
  chartInstances.forEach(chart => chart.resize())
}

onMounted(() => {
  initLineChart()
  initRadarChart()
  initPieChart()
  initDonutChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstances.forEach(chart => chart.dispose())
})
</script>

<style scoped>
.analytics-container {
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}

/* 顶部数据卡片 */
.panel-group {
  margin-bottom: 16px;
}

.card-panel {
  background: #fff;
  border-radius: 5px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 140px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.card-panel:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-panel-text {
  font-size: 14px;
  color: #666;
}

.card-panel-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.card-panel-num {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.card-panel-icon {
  width: 48px;
  height: 48px;
}

.card-panel-footer {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
}

/* 中间主图表 */
.chart-row {
  margin-bottom: 16px;
}

.main-chart-card {
  border-radius: 5px;
  border: none;
}

:deep(.main-chart-card .el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 12px 20px;
}

.chart-header .tabs {
  display: flex;
  gap: 20px;
}

.chart-header .tab {
  cursor: pointer;
  font-size: 14px;
  color: #666;
  padding-bottom: 8px;
}

.chart-header .tab.active {
  color: #1890ff;
  border-bottom: 2px solid #1890ff;
  font-weight: 500;
}

.main-chart {
  height: 350px;
  width: 100%;
}

/* 底部子图表 */
.bottom-chart-row .sub-chart-card {
  border-radius: 5px;
  border: none;
  margin-bottom: 16px;
}

:deep(.sub-chart-card .el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  font-weight: 500;
}

.sub-chart {
  height: 280px;
  width: 100%;
}
</style>