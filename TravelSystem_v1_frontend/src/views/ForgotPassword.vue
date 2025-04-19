<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <h2>忘记密码</h2>
      <form @submit.prevent="handleForgotPassword">
        <div class="input-group">
          <input
            v-model="forgotPasswordForm.email"
            type="email"
            placeholder="请输入邮箱"
            required
          />
        </div>
        <button type="submit" class="forgot-password-button">发送重置链接</button>
      </form>
      <div class="links">
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const forgotPasswordForm = ref({
  email: '',
});

const handleForgotPassword = () => {
  // 模拟发送重置链接
  if (forgotPasswordForm.value.email) {
    alert('重置链接已发送至您的邮箱');
    router.push('/login'); // 跳转到登录页面
  } else {
    alert('请输入邮箱');
  }
};
</script>

<style scoped>
.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(0deg,#d3e2f7, #ffffff);
}

.forgot-password-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1rem;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
}

.forgot-password-button {
  width: 100%;
  padding: 0.75rem;
  background: #2c2fff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
}

.links {
  margin-top: 1rem;
  text-align: center;
}
</style>