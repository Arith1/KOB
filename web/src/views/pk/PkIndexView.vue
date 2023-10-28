<template>
    <!-- <ContentField></ContentField> -->
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser != 'none'" />
</template>

<script>
// import ContentField from '@/components/ContentField.vue';
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import ResultBoard from '@/components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
//onMounted是当组件挂载之后 
//onUnmounted是卸载之后 
export default {
    components: {
        // ContentField,
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://localhost:3080/websocket/${store.state.user.token}/`;

        store.commit("updateIsRecord", false);//恢复

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
                    }, 200);
                    store.commit("updateGame", data.game);
                }else if (data.event === "move"){
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                }else if (data.event === "result"){
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    if (data.loser === "all" || data.loser === "A"){
                        snake0.status = "die";
                    }
                    if (data.loser === "all" || data.loser === "B"){
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            }


            socket.onclose = () => {
                //关闭时
                console.log("disconnected");
            }
        });

        onUnmounted(() => {
            store.commit("updateLoser", "none");
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