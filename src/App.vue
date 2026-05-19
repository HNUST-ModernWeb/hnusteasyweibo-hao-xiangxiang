<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import LoginModal from './components/LoginModal.vue'
import ImageUploader from './components/ImageUploader.vue'
import SockJS from 'sockjs-client'
import Stomp from '@stomp/stompjs'

const API_BASE = 'http://localhost:8080/api'

const posts = ref([])
const newPost = ref('')
const newPostImages = ref([])
const loading = ref(false)
const showLogin = ref(false)
const currentUser = ref(null)

let stompClient = null

// 连接 WebSocket（实时点赞）
function connectWebSocket() {
  const socket = new SockJS('http://localhost:8080/ws')
  stompClient = Stomp.over(socket)
  
  stompClient.connect({}, () => {
    // 订阅所有帖子的点赞更新
    stompClient.subscribe('/topic/likes/**', (message) => {
      const data = JSON.parse(message.body)
      // 更新对应帖子的点赞数
      const post = posts.value.find(p => p.id === data.postId)
      if (post) {
        post.likes = data.likeCount
      }
    })
  })
}

// 检查登录状态
async function checkLogin() {
  try {
    const response = await axios.get(`${API_BASE}/auth/me`, { withCredentials: true })
    if (response.data.success) {
      currentUser.value = response.data.user
    }
  } catch (error) {
    console.error('检查登录失败')
  }
}

// 加载帖子
async function loadPosts() {
  loading.value = true
  try {
    const response = await axios.get(`${API_BASE}/posts`)
    posts.value = response.data
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

// 发布帖子
async function publishPost() {
  if (!newPost.value.trim() && newPostImages.value.length === 0) {
    alert('请填写内容或添加图片')
    return
  }
  if (!currentUser.value) {
    showLogin.value = true
    return
  }
  
  try {
    await axios.post(`${API_BASE}/posts`, {
      content: newPost.value,
      images: newPostImages.value.join(',')
    }, { withCredentials: true })
    await loadPosts()
    newPost.value = ''
    newPostImages.value = []
  } catch (error) {
    alert('发布失败')
  }
}

// 点赞
async function likePost(postId) {
  if (!currentUser.value) {
    showLogin.value = true
    return
  }
  
  try {
    await axios.post(`${API_BASE}/posts/${postId}/like`, {}, { withCredentials: true })
    // WebSocket 会实时更新点赞数，不需要重新加载
  } catch (error) {
    console.error('点赞失败', error)
  }
}

// 登出
async function logout() {
  await axios.post(`${API_BASE}/auth/logout`, {}, { withCredentials: true })
  currentUser.value = null
}

// 登录成功
function onLoginSuccess(user) {
  currentUser.value = user
  loadPosts()
}

// 处理图片显示
function getImages(imagesStr) {
  if (!imagesStr) return []
  return imagesStr.split(',').filter(url => url.trim())
}

onMounted(() => {
  checkLogin()
  loadPosts()
  connectWebSocket()
})

onUnmounted(() => {
  if (stompClient) {
    stompClient.disconnect()
  }
})
</script>

<template>
  <div class="container">
    <div class="header">
      <h1>🌟 我的社区</h1>
      <div class="user-info">
        <div v-if="currentUser" class="user-menu">
          <span>欢迎，{{ currentUser.nickname || currentUser.username }}</span>
          <button @click="logout" class="logout-btn">退出</button>
        </div>
        <button v-else @click="showLogin = true" class="login-btn">登录 / 注册</button>
      </div>
    </div>
    
    <div v-if="loading" class="loading">加载中...</div>
    
    <div class="publish-box">
      <textarea 
        v-model="newPost"
        placeholder="分享你的想法..."
        rows="3"
      ></textarea>
      
      <ImageUploader v-model="newPostImages" />
      
      <button @click="publishPost">发布动态</button>
    </div>
    
    <div v-for="post in posts" :key="post.id" class="post-card">
      <div class="post-header">
        <span class="author">{{ post.nickname || post.username || '用户' }}</span>
        <span class="time">{{ new Date(post.createdAt).toLocaleString() }}</span>
      </div>
      <p class="content">{{ post.content }}</p>
      
      <div v-if="post.images" class="post-images">
        <img 
          v-for="(img, idx) in getImages(post.images)" 
          :key="idx"
          :src="img"
          class="post-image"
        />
      </div>
      
      <button @click="likePost(post.id)" class="like-btn">
        ❤️ {{ post.likes }}
      </button>
    </div>
    
    <LoginModal 
      v-if="showLogin" 
      @close="showLogin = false"
      @login-success="onLoginSuccess"
    />
  </div>
</template>

<style scoped>
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h1 {
  color: #1e88e5;
}

.login-btn, .logout-btn {
  background-color: #1e88e5;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn {
  background-color: #999;
  margin-left: 10px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.loading {
  text-align: center;
  color: #666;
  padding: 20px;
}

.publish-box {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  font-size: 14px;
  box-sizing: border-box;
  resize: vertical;
}

button {
  margin-top: 10px;
  background-color: #1e88e5;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.post-card {
  background: white;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
}

.author {
  color: #1e88e5;
  font-weight: bold;
}

.time {
  color: #999;
}

.content {
  margin-bottom: 10px;
  color: #333;
}

.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 10px 0;
}

.post-image {
  max-width: 150px;
  max-height: 150px;
  border-radius: 4px;
  object-fit: cover;
}

.like-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  margin: 0;
  padding: 0;
}

.like-btn:hover {
  color: #e91e63;
}
</style>