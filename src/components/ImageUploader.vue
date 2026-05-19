<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import axios from 'axios'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const images = ref([...props.modelValue])
const uploading = ref(false)
const uploadProgress = ref(0)

const API_BASE = 'http://localhost:8080/api'

// 上传图片
async function uploadImage(event) {
  const files = event.target.files
  if (!files.length) return
  
  const file = files[0]
  
  // 检查文件大小（限制5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片不能超过5MB')
    return
  }
  
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    alert('只能上传图片文件')
    return
  }
  
  uploading.value = true
  uploadProgress.value = 0
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const response = await axios.post(`${API_BASE}/files/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      withCredentials: true,
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        uploadProgress.value = percentCompleted
      }
    })
    
    if (response.data.success) {
      images.value.push(response.data.url)
      emit('update:modelValue', images.value)
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    console.error('上传失败', error)
    alert('上传失败，请重试')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

// 删除图片
function removeImage(index) {
  images.value.splice(index, 1)
  emit('update:modelValue', images.value)
}
</script>

<template>
  <div class="image-uploader">
    <!-- 已上传的图片列表 -->
    <div class="image-list" v-if="images.length > 0">
      <div v-for="(img, index) in images" :key="index" class="image-item">
        <img :src="img" class="preview-image" />
        <button @click="removeImage(index)" class="remove-btn">✕</button>
      </div>
    </div>
    
    <!-- 上传按钮 -->
    <div class="upload-area" v-if="!uploading">
      <label class="upload-btn">
        📷 添加图片
        <input 
          type="file" 
          accept="image/*" 
          @change="uploadImage"
          style="display: none"
        />
      </label>
      <span class="hint">支持jpg、png、gif，最多5MB</span>
    </div>
    
    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <span>上传中 {{ uploadProgress }}%</span>
    </div>
  </div>
</template>

<style scoped>
.image-uploader {
  margin: 10px 0;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  background: #e91e63;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin: 0;
}

.upload-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.upload-btn {
  display: inline-block;
  background-color: #f0f0f0;
  color: #666;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.upload-btn:hover {
  background-color: #e0e0e0;
}

.hint {
  font-size: 12px;
  color: #999;
}

.upload-progress {
  margin: 10px 0;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 5px;
}

.progress-fill {
  height: 100%;
  background-color: #1e88e5;
  transition: width 0.3s;
}
</style>