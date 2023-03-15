<script setup>
import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { onMounted } from 'vue'
import "xterm/css/xterm.css"
import { AttachAddon } from 'xterm-addon-attach'

const op = {
  rendererType: "canvas", //渲染类型
  convertEol: true, //启用时，光标将设置为下一行的开头
  scrollback: 500, //终端中的回滚量
  disableStdin: false, //是否应禁用输入
  cursorStyle: "bar", //光标样式
  cursorBlink: true, //光标闪烁
  fontSize: 17,
  theme: {
    lineHeight: 16
  }
}
const term = new Terminal(op)
if (window.location.protocol == 'https:') {
  var protocol = 'wss://'
} else {
  var protocol = 'ws://'
}
const socket = new WebSocket(`${protocol}${location.host}/webssh`)
const attachAddon = new AttachAddon(socket)
const fitAddon = new FitAddon()
term.loadAddon(attachAddon)
term.loadAddon(fitAddon)
window.addEventListener("resize", () => {
  try {
    fitAddon.fit()
  } catch (e) {
    console.log("e", e.message)
  }
})

onMounted(() => {
  term.open(document.getElementById('divDom'))
  fitAddon.fit()
})
</script>

<template>
  <div id="divDom"></div>
</template>

<style scoped>
</style>