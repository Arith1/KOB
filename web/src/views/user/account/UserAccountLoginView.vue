<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">  
                    <!-- 绑定setup中的 login事件 pervent起保护作用 -->
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                        <!-- v-model 绑定setup中的username变量 -->
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import router from '@/router/index'

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        const login = () => {
            error_message.value = ""; //提交完后error_message记得清空
            store.dispatch("login", {   //dispatch用来调用store中的
                username: username.value,
                password: password.value,
                success() {
                    // console.log(resp)//登录成功后
                    store.dispatch("getinfo", {
                        success() { //获取info，用户的id，用户名等信息，然后跳转home
                            router.push({ name: 'home' });
                            console.log(store.state.user);
                        }
                    })
                },
                error() {
                    error_message.value = "用户名或密码错误";
                }
            })
        }

        return {
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}
</style>
