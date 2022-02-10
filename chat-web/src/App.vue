
<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png"> -->
    <!-- <HelloWorld msg="Welcome to Your Vue.js App"/> -->
		<router-view />
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'app',
  // components: {
  //   HelloWorld
  // },
	data() {
    return {
      isRouterAlive: false
    }
  },

	mounted() {
		// debugger
		// this.$router.push({ path:'/index' });
	}

	
}
	
</script>

<style>
/**
*	全局背景图
*/
body {
	width: 100%;
	height: 100%;
	z-index: -1;
	position: fixed;
	top: 0px;
	left: 0px;
	bottom: 0px;
	right: 0px;
	background-image: url('assets/bg.png');
}
</style>
