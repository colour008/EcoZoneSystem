<script setup>

import {Location} from "@element-plus/icons-vue";
</script>

<template>
  <div class="form-section mt-4">
    <h4 class="section-title">
      <el-icon>
        <Location/>
      </el-icon>
      园区入驻意向
    </h4>
    <el-row :gutter="30">
      <el-col :span="12">
        <el-form-item label="意向楼宇" prop="buildingNo">
          <el-input v-model="form.buildingNo" placeholder="例如：A座-808"/>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="租用面积" prop="rentArea">
          <el-input-number v-model="form.rentArea" :precision="2" :min="0" controls-position="right"
                           style="width: 100%"/>
          <span class="input-suffix">㎡</span>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="租约期限" prop="leaseEndDate" class="date-range-item">
          <el-date-picker
              v-model="form.leaseStartDate"
              type="date"
              placeholder="起租日期"
              value-format="YYYY-MM-DD"
              style="width: 47%"
          />
          <span class="date-separator">至</span>
          <el-date-picker
              v-model="form.leaseEndDate"
              type="date"
              placeholder="到期日期"
              value-format="YYYY-MM-DD"
              style="width: 47%"
          />
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>

</style>