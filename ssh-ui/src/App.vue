<script setup>
import ssh from '@/components/ssh.vue'
import file from '@/components/file.vue'
</script>

<template>
  <el-container style=" height:100vh">
    <el-header class="homeHeader"
               height="60px">
      <div class="span">
        服务器管理系统
      </div>
    </el-header>
    <el-main class="main">
      <ssh class="ssh"></ssh>
      <file></file>
    </el-main>
  </el-container>
</template>

<style scoped>
.main {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}
.homeHeader {
  display: flex;
  justify-content: space-between;
  background: #406dbb;
}
.span {
  margin: auto 10px;
  font-size: 30px;
  font-family: cursive;
  color: rgb(226, 235, 235);
}
.ssh {
  height: 70%;
  width: 90%;
}
</style>
