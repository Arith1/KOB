<template>
    <div class="playground">
        <GameMap />
    </div>
</template>

<script>
    import GameMap from '@/components/GameMap.vue'
    export default {
        components:{
            GameMap
        }
    }
</script>

<style scoped>
    /* Your style code here */
    div.playground{
        width: 60vw;
        height: 70vh;
        margin: 40px auto;
    }
</style>
