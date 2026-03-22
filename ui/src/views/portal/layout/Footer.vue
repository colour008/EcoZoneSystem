<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-info">
        <h3>经济开发区管理平台</h3>
        <p>地址：开发区创业大道 88 号智慧大厦</p>
        <p>电话：0512-88888888</p>
      </div>
      <div class="footer-links">
        <el-link :underline="false">政策法规</el-link>
        <el-link :underline="false">办事指南</el-link>
        <el-link :underline="false">联系我们</el-link>
      </div>
    </div>
    <div class="footer-bottom">
      Copyright © 2026 JamHoo. All Rights Reserved.
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: #2b2f3a;
  color: #afb1b3;
  padding: 40px 0 20px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.footer-info h3 {
  color: #fff;
  margin-bottom: 20px;
}

.footer-info p {
  font-size: 14px;
  line-height: 2;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer-links .el-link {
  color: #afb1b3;
}

.footer-links .el-link:hover {
  color: #409eff;
}

.footer-bottom {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #3d424d;
  font-size: 12px;
}
</style>