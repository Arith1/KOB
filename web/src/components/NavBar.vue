<template>
  <nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container">
      <router-link class="navbar-brand" :to="{ name: 'home' }">主页</router-link>

      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" aria-current="page"
              :to="{ name: 'pk_index' }">对战</router-link>
          </li>
          <li class="nav-item">

            <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'"
              :to="{ name: 'record_index' }">对局列表</router-link>
          </li>
          <li class="nav-item">

            <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'"
              :to="{ name: 'ranklist_index' }">排行榜</router-link>
          </li>
        </ul>


        <!-- 通过user中的is_login判断是否登录状态 -->
        <ul class="navbar-nav" v-if="$store.state.user.is_login">
          <li class="nav-item dropdown">
            <a class="nav-link  dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              {{ $store.state.user.username }}
              <!-- 显示当前登录的用户名 -->
            </a>
            <ul class="dropdown-menu navbar-dark" style="background-color: transparent; border: 1px solid #ccccd6;">
              <li>
                <router-link class="dropdown-item" :to="{ name: 'user_bot_index' }"
                  style="color:#ccccd6;">我的bot</router-link>
              </li>

              <li><a class="dropdown-item" @click="logout" href="#" style="color:#ccccd6;">退出</a></li>
            </ul>
          </li>
        </ul>

        <ul class="navbar-nav" v-else>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'user_account_login' }" role="button">
              登录
            </router-link>
          </li>
          <li class="nav-item">
            <a class="nav-link" :to="{ name: 'user_account_register' }" role="button">
              注册
            </a>
          </li>
        </ul>


      </div>
    </div>
  </nav>
</template>

<script>
// 获取当前活动页面
import { useRoute } from 'vue-router'
import { computed } from 'vue';
import { useStore } from 'vuex';

export default {
  setup() {
    const route = useRoute();
    let route_name = computed(() => route.name);


    const store = useStore();
    const logout = () => {
      store.dispatch("logout");
    }


    return {
      route_name,
      logout
    }
  }
}
</script>

<style>
.container {
  color: aliceblue;
  margin-right: 20px;
}
</style>