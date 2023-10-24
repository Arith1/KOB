<template>
    <!-- <ContentField></ContentField> -->
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
</template>

<script>
// import ContentField from '@/components/ContentField.vue';
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
//onMounted是当组件挂载之后 
//onUnmounted是卸载之后 
export default {
    components: {
        // ContentField,
        PlayGround,
        MatchGround
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://localhost:3080/websocket/${store.state.user.token}/`;

        let socket = null;
        onMounted(() => {

            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            });

            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                //连接时
                console.log("connected");
                //连接后更新socket
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                console.log(data);
                if (data.event === "start-matching") {  // 匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    //成功推迟3秒后转移到gamemap页面
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 3000);
                    store.commit("updateGamemap", data.gamemap);
                }
            }


            socket.onclose = () => {
                //关闭时
                console.log("disconnected");
            }
        });

        onUnmounted(() => {
            //断开链接,否则每次打开都会创建新的链接
            socket.close();
            store.commit("updateStatus", "matching");//断开后，需要重新匹配
        })
    }
}
</script>


<style scoped>
div {
    color: #ccccd6
}
</style>