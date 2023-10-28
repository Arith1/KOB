<template>

    <PlayGround/>

</template>

<script>

import PlayGround from '@/components/PlayGround.vue'

//onMounted是当组件挂载之后 
//onUnmounted是卸载之后 
export default {
    components: {

        PlayGround,

    },
    setup() {
    }
}

</script>


<style scoped>
div {
    color: #ccccd6
}
</style>