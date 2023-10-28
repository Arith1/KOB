<template>
    <ContentField>
        <table class="table table-white table-hover" style="text-align: center;">
            <thead>
                <tr>
                    <th>A</th>
                    <th>B</th>
                    <th>Winner</th>
                    <th>Time</th>
                    <th>op</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="record in records" :key="record.record.id">
                    <td>
                        <img :src="record.a_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ record.a_username }}</span>
                    </td>
                    <td>
                        <img :src="record.b_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ record.b_username }}</span>
                    </td>
                    <td>
                        {{ record.result }}
                    </td>
                    <td>
                        {{ record.record.createtime }}
                    </td>
                    <td>
                        <button @click="open_record_content(record.record.id)" type="button"
                            class="btn btn-secondary">View</button>
                    </td>
                </tr>
            </tbody>

        </table>
        <nav aria-label="...">
            <ul class="pagination" style="float: right;">
                <li class="page-item">
                    <a class="page-link" href="#" @click="click_page(-2)">Previous</a>
                </li>
                <!-- <li :class="'page-item '+ page.is_active" v-for="page in pages" :key="page.number">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li> -->
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>

                <li class="page-item" @click="click_page(-1)">
                    <a class="page-link" href="#">Next</a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { useStore } from "vuex";
import $ from 'jquery';
import { ref } from 'vue';
import router from '@/router/index';
export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let records = ref([]);
        let current_page = 1;
        let total_records = 0;

        let pages = ref([]);

        const click_page = page => {
            if (page === -2) page = current_page - 1;   //前一页
            else if (page === -1) page = current_page + 1; //后一页
            let max_pages = parseInt(Math.ceil(total_records / 10));

            if (page >= 1 && page <= max_pages) {
                pull_page(page);
            }
        }


        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(total_records / 10));        //计算最多多少页
            // console.log(max_pages);
            let new_pages = [];                                             
            for (let i = current_page - 2; i <= current_page + 2; i++) {    //每次显示5页
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,  //page id
                        is_active : i === current_page ? "active" : "",
                    });
                }
            }
            pages.value = new_pages;
            // console.log(pages.value);
        }

        const pull_page = page => {
            current_page = page;
            $.ajax({
                url: "http://127.0.0.1:3080/record/getlist/",
                type: "get",
                data: {
                    page,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    // console.log(store.state.user.token);
                    // console.log(resp);
                    records.value = resp.records;
                    console.log(records.value);
                    total_records = resp.records_count;
                    // console.log(total_records);
                    update_pages();//更新页面
                }
            })
        }
        pull_page(current_page);

        const stringTo2D = map => {
            let g = [];
            for (let i = 0, k = 0; i < 13; i++) {
                let line = [];
                for (let j = 0; j < 14; j++, k++) {
                    if (map[k] === '0') line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }



        const open_record_content = recordId => {
            for (const record of records.value) {
                if (record.record.id === recordId) {
                    store.commit("updateIsRecord", true);
                    store.commit("updateGame", {
                        map: stringTo2D(record.record.map),
                        a_id: record.record.aid,
                        a_sx: record.record.asx,
                        a_sy: record.record.asy,
                        b_id: record.record.bid,
                        b_sx: record.record.bsx,
                        b_sy: record.record.bsy,
                    });
                    store.commit("updateSteps", {
                        a_steps: record.record.asteps,
                        b_steps: record.record.bsteps,
                    });
                    store.commit("updateRecordLoser", record.record.loser);
                    router.push({       //跳转
                        name: "record_content",
                        params: {
                            recordId
                        }
                    })
                    break;
                }
            }
        }

        return {
            records,
            open_record_content,
            total_records,
            update_pages,
            pages,
            click_page,
        }
    }
}
</script>


<style scoped>
div {
    color: #ccccd6
}

img.record-user-photo {
    border-radius: 50%;
    width: 5vh;
}
</style>