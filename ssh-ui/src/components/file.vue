<template>
  <el-upload ref="upload"
             :action="'/upload?path='+input"
             :limit="1"
             :on-exceed="handleExceed"
             :auto-upload="false">
    <template #trigger>
      <el-button type="primary">选择文件</el-button>
    </template>
    <el-button class="ml-3"
               type="success"
               @click="submitUpload">
      上传到服务器
    </el-button>
    <template #tip>
      <el-input v-model="input"
                placeholder="上传路径" /><br>
      <br>
      <el-alert title="路径必须以 / 开头，文件名结尾"
                type="warning" /><br>
      <el-input v-model="downloadInput"
                placeholder="下载路径" /><br>
      <el-button type="success"
                 @click="download">下载</el-button>
    </template>
  </el-upload>

</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, genFileId } from "element-plus";
import type { UploadInstance, UploadProps, UploadRawFile } from "element-plus";
const downloadInput = ref("");
const input = ref("");
const upload = ref<UploadInstance>();
const handleExceed: UploadProps["onExceed"] = (files) => {
  upload.value!.clearFiles();
  const file = files[0] as UploadRawFile;
  file.uid = genFileId();
  upload.value!.handleStart(file);
};

const submitUpload = () => {
  if (!input.value) {
    ElMessage.error("路径不能为空");
    return;
  }
  upload.value!.submit();
};
const download = () => {
  if (!downloadInput.value) {
    ElMessage.error("路径不能为空");
    return;
  }
  let link = document.createElement("a");
  link.href = "/file" + downloadInput.value;
  // link.download = file.name;
  link.target = "_blank";
  link.click();
};
</script>
