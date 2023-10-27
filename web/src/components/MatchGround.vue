<!-- 匹配界面 -->
<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example" :disabled="match_btn_info === 'Chanel'">
                        <option value="-1" selected>By Yourself</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.title }}
                        </option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center; padding-top: 15vh;">
                <button @click="click_match_btn" type="button" class="btn btn-warning btn-lg">{{ match_btn_info }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import  $  from 'jquery'
import { useStore } from 'vuex';

export default {
    setup() {
        const store = useStore();
        let match_btn_info = ref("Ready");

        let bots = ref([]);
        let select_bot = ref("-1");

        const refresh_bots = () => {    //getlist
            $.ajax({
                url: "http://127.0.0.1:3080/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            })
        }

        const click_match_btn = () => {
            if (match_btn_info.value === "Ready") {
                match_btn_info.value = "Chanel";
                console.log(select_bot.value);
                store.state.pk.socket.send(JSON.stringify({
                    username: store.state.user.username,
                    event: "start-matching",
                    bot_id: select_bot.value,
                }));
            } else {
                match_btn_info.value = "Ready";
                store.state.pk.socket.send(JSON.stringify({
                    username: store.state.user.username,
                    event: "stop-matching",
                }));
            }
        }

        refresh_bots(); //获取列表

        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }

}
</script>

<style scoped>
/* Your style code here */
div.matchground {
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
    background-color: rgba(53, 49, 49, 0.5);
}

div.user-photo {
    padding-top: 10vh;
    text-align: center;

}

div.user-photo>img {
    width: 20vh;
    border-radius: 50%;
}

div.user-username {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
}
div.user-select-bot{
    padding-top: 20vh;
}
div.user-select-bot > select {
    width : 60%;
    margin: 0 auto; 
    /* 剧中 */
}
</style>
