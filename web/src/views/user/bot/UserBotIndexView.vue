<template>
    <!-- <ContentField>mybot</ContentField> -->

    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 25px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 25px;">
                    <div class="card-header">
                        <span style="font-size: 140%;">Mybot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal"
                            data-bs-target="#add_bot">
                            New bot
                        </button>
                        <!-- Modal -->
                        <div class="modal fade" id="add_bot" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5" id="exampleModalLabel">New Bot
                                        </h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <!-- form -->
                                        <div class="mb-3">
                                            <label for="Bot-Title" class="form-label">Bot name</label>
                                            <!-- 绑定botadd.title -->
                                            <input v-model="botadd.title" type="text" class="form-control"
                                                id="exampleFormControlInput1" placeholder="Please enter your bot name">
                                        </div>
                                        <div class="mb-3">
                                            <label for="Bot-description" class="form-label">Bot
                                                description</label>
                                            <!-- 绑定botadd.description -->
                                            <textarea v-model="botadd.description" class="form-control"
                                                id="exampleFormControlTextarea1" rows="3"
                                                placeholder="Please enter your bot description"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="Bot-description" class="form-label">Bot
                                                code</label>
                                            <!-- 绑定botadd.content -->
                                            <!-- <textarea v-model="botadd.content" class="form-control"
                                                id="exampleFormControlTextarea1" rows="7"
                                                placeholder="Please enter your bot code"></textarea> -->
                                            <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp"
                                                theme="textmate" style="height: 300px" :options="{
                                                    enableBasicAutocompletion: true, //启用基本自动完成
                                                    enableSnippets: true, // 启用代码段
                                                    enableLiveAutocompletion: true, // 启用实时自动完成
                                                    fontSize: 18, //设置字号
                                                    tabSize: 4, // 标签大小
                                                    showPrintMargin: false, //去除编辑器里的竖线
                                                    highlightActiveLine: true,
                                                    color
                                                }" />
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error_message" style="color: red;">{{ botadd.error_message }}</div>

                                        <button @click="add_bot" type="button" class="btn btn-primary">Create</button>
                                        <button type="button" class="btn btn-secondary"
                                            data-bs-dismiss="modal">Cancel</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-white table-hover">
                            <thead>
                                <tr>
                                    <th>botname</th>
                                    <th>createtime</th>
                                    <th>operation</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px;"
                                            data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">
                                            update
                                        </button>
                                        <!-- 转递一个bot回去 -->
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">
                                            delete
                                        </button>
                                        <!-- modal 因为bots是个列表所以第一个模态框应该对应一个botid -->
                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h1 class="modal-title fs-5" style="color: black;">Update Bot
                                                        </h1>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                            aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body" style="color: black;">
                                                        <!-- form -->
                                                        <div class="mb-3">
                                                            <label for="Bot-Title" class="form-label"
                                                                style="color: black;">Bot name</label>
                                                            <!-- 绑定botadd.title -->
                                                            <input v-model="bot.title" type="text" class="form-control"
                                                                id="exampleFormControlInput1">
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="Bot-description" class="form-label"
                                                                style="color: black;">Bot
                                                                description</label>
                                                            <!-- 绑定botadd.description -->
                                                            <textarea v-model="bot.description" class="form-control"
                                                                id="exampleFormControlTextarea1" rows="3"></textarea>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="Bot-description" class="form-label"
                                                                style="color: black;">Bot
                                                                code</label>
                                                            <!-- 绑定botadd.content -->
                                                            <!-- <textarea v-model="bot.content" class="form-control"
                                                                id="exampleFormControlTextarea1" rows="7"></textarea> -->
                                                            <!-- 改为代码编辑器 -->
                                                            <VAceEditor v-model:value="bot.content" @init="editorInit"
                                                                lang="c_cpp" theme="textmate" style="height: 300px"
                                                                :options="{
                                                                    enableBasicAutocompletion: true, //启用基本自动完成
                                                                    enableSnippets: true, // 启用代码段
                                                                    enableLiveAutocompletion: true, // 启用实时自动完成
                                                                    fontSize: 18, //设置字号
                                                                    tabSize: 4, // 标签大小
                                                                    showPrintMargin: false, //去除编辑器里的竖线
                                                                    highlightActiveLine: true,
                                                                    color
                                                                }" />
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <div class="error_message" style="color: red;">{{
                                                            update_bot.error_message }}</div>

                                                        <button @click="update_bot(bot)" type="button"
                                                            class="btn btn-primary">Submit</button>
                                                        <button type="button" class="btn btn-secondary"
                                                            data-bs-dismiss="modal">Cancel</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
// import ContentField from '@/components/ContentField.vue';

import $ from 'jquery'
import { useStore } from 'vuex'
import { ref, reactive } from "vue"

// 引入这个js可以实现点击创建后自动关闭modal
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
//active用于和对象form绑定

export default {
    components: {
        // ContentField
        VAceEditor
    },
    setup() {

        ace.config.set(
            "basePath",
            "https://fastly.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

        const store = useStore();
        let bots = ref([]); //定义bot数组用来存放get获取的bots

        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        })

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

        refresh_bots(); //定义完成后要执行

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3080/user/bot/add/",
                type: "POST",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        //getInstance里面是modal的id，id前一定要加#
                        Modal.getInstance("#add_bot").hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message
                    }
                }
            })
        }

        //bot为参数，传递一个id
        const remove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3080/user/bot/remove/",
                type: "POST",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message
                    }
                }
            })
        }

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3080/user/bot/update/",
                type: "POST",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        //getInstance里面是modal的id，id前一定要加#
                        Modal.getInstance("#update-bot-modal-" + bot.id).hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message
                    }
                }
            })
        }

        return {
            bots,   //返回bots数组
            botadd,
            add_bot,
            remove_bot,
            update_bot,
        }
    }

}
</script>


<style scoped></style>