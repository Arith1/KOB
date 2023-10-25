<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"></canvas>
        <!-- tabindex可以获取用户输入操作 -->
    </div>
</template>

<script>
import { GameMap } from "@/assets/scripts/GameMap";
import { ref, onMounted } from 'vue'
import { useStore } from "vuex";

export default {
    setup() {
        let parent = ref(null);
        let canvas = ref(null);

        const store = useStore();

        onMounted(() => {
            store.commit(   //装object存入到全局变量中
                "updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store) //传过sotre
            )
        });

        return {
            parent,
            canvas
        }
    }
}
</script>

<style scoped>
div.gamemap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
