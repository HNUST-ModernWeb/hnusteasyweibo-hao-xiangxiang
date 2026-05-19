<script setup>
// 定义接收的数据
defineProps({
  post: {
    type: Object,
    required: true
  }
})

// 定义事件
const emit = defineEmits(['like'])

function handleLike() {
  emit('like', post.id)
}
</script>

<template>
  <div class="post-card">
    <p class="content">{{ post.content }}</p>
    <button @click="handleLike" class="like-btn">
      ❤️ {{ post.likes }}
    </button>
  </div>
</template>

<style scoped>
.post-card {
  background: white;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.content {
  margin-bottom: 10px;
  color: #333;
}
.like-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
}
.like-btn:hover {
  color: #e91e63;
}
</style>