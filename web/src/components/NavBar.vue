<template>
  <nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container">
      <router-link class="navbar-brand" :to="{ name: 'home' }">主页</router-link>

      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" aria-current="page" :to="{ name: 'pk_index' }">对战</router-link>
          </li>
          <li class="nav-item">

            <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{ name: 'record_index' }">对局列表</router-link>
          </li>
          <li class="nav-item">

            <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{ name: 'ranklist_index' }">排行榜</router-link>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li class="nav-item dropdown">
            <a class="nav-link  dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              Miyan
            </a>
            <ul class="dropdown-menu navbar-dark" style="background-color: transparent; border: 1px solid #ccccd6;">
              <li>
                <router-link class="dropdown-item" :to="{ name: 'user_bot_index' }" style="color:#ccccd6;">我的bot</router-link>
              </li>
              
              <li><a class="dropdown-item" href="#" style="color:#ccccd6;">退出</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
// 获取当前活动页面
import {useRoute} from 'vue-router'
import { computed } from 'vue';
export default {
  setup() {
    const route = useRoute();
    let route_name = computed(() => route.name)

    return{
      route_name
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