<script setup>
import { ref, defineEmits } from 'vue'
import axios from 'axios'

const emit = defineEmits(['close', 'login-success'])

const isLogin = ref(true)  // true: 登录, false: 注册
const username = ref('')
const password = ref('')
const nickname = ref('')
const errorMsg = ref('')
const loading = ref(false)

const API_BASE = 'http://localhost:8080/api'

async function handleSubmit() {
  errorMsg.value = ''
  loading.value = true
  
  try {
    if (isLogin.value) {
      // 登录
      const response = await axios.post(`${API_BASE}/auth/login`, {
        username: username.value,
        password: password.value
      }, { withCredentials: true })
      
      if (response.data.success) {
        emit('login-success', response.data.user)
        emit('close')
      } else {
        errorMsg.value = response.data.message
      }
    } else {
      // 注册
      const response = await axios.post(`${API_BASE}/auth/register`, {
        username: username.value,
        password: password.value,
        nickname: nickname.value || username.value
      })
      
      if (response.data.success) {
        // 注册成功后自动切换到登录
        isLogin.value = true
        errorMsg.value = '注册成功，请登录'
        password.value = ''
      } else {
        errorMsg.value = response.data.message
      }
    }
  } catch (error) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

function switchMode() {
  isLogin.value = !isLogin.value
  errorMsg.value = ''
  username.value = ''
  password.value = ''
  nickname.value = ''
}
</script>

<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <h2>{{ isLogin ? '登录' : '注册' }}</h2>
      
      <div class="form-group">
        <input 
          v-model="username"
          type="text"
          placeholder="用户名"
          class="input"
        />
      </div>
      
      <div class="form-group">
        <input 
          v-model="password"
          type="password"
          placeholder="密码"
          class="input"
          @keyup.enter="handleSubmit"
        />
      </div>
      
      <div class="form-group" v-if="!isLogin">
        <input 
          v-model="nickname"
          type="text"
          placeholder="昵称（可选）"
          class="input"
        />
      </div>
      
      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
      
      <button @click="handleSubmit" class="submit-btn" :disabled="loading">
        {{ loading ? '请稍后...' : (isLogin ? '登录' : '注册') }}
      </button>
      
      <p class="switch-mode" @click="switchMode">
        {{ isLogin ? '还没有账号？立即注册' : '已有账号？去登录' }}
      </p>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 350px;
  max-width: 90%;
}

h2 {
  margin-bottom: 20px;
  text-align: center;
}

.form-group {
  margin-bottom: 15px;
}

.input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.submit-btn {
  width: 100%;
  background-color: #1e88e5;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-msg {
  color: #e91e63;
  font-size: 12px;
  margin-bottom: 10px;
  text-align: center;
}

.switch-mode {
  text-align: center;
  margin-top: 15px;
  color: #1e88e5;
  cursor: pointer;
  font-size: 14px;
}

.switch-mode:hover {
  text-decoration: underline;
}
</style>