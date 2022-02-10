<template>
  <div style="ba">
    <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-box">
      <h3 class="login-title">进入聊天</h3>
      <el-form-item label="用户名" prop="username">
        <el-input type="text" placeholder="请输入用户名" v-model="form.username" maxlength="4"/>
      </el-form-item>

			<el-form-item label="头像" prop="head">
				<el-input ref="head" type="text" :readonly="true" @focus="dialogVisible = true"/>
			</el-form-item>
      <el-form-item>
        <el-button type="primary" v-on:click="onSubmit('loginForm')">登录</el-button>
      </el-form-item>
    </el-form>
    <el-dialog
			title="选择头像"
      :visible.sync="dialogVisible"
      width="400px"
			>
			<div class="panel-head">
				<div v-for="(item,index) in headImgs" :key="index">
					<img :src="item.label" @click="showHeadImg(item)"/>
				</div>
			</div>
    </el-dialog>
  </div>
</template>

<script>
import util from '@/utils/util';
import store from '@/store/index';
export default {
	name: "Login",
	data() {
		return {
			util,
			form: {
				username: '',
				password: '',
				head: ''
			},
			// 表单验证，需要在 el-form-item 元素中增加 prop 属性
			rules: {
				username: [
					{required: true, message: '用户名不可为空', trigger: 'blur'}
				],
				password: [
					{required: true, message: '密码不可为空', trigger: 'blur'}
				],
				head: [
					{required: true, message: '头像不可为空', trigger: 'blur'}
				]
			},
			headImgs:[],
			// 对话框显示和隐藏
			dialogVisible: false,
			// 防止表单重复提交
			lock: false,  
		}
	},
	methods: {
		onSubmit(formName) {
			if(this.lock) {
				return ;
			}
			console.log(this.form);
			this.lock = true;
			// 为表单绑定验证功能
			this.$refs[formName].validate((valid) => {
				this.lock = false;
				if (valid) {
					// 使用 vue-router 路由到指定页面，该方式称之为编程式导航
					this.createUser();
					this.$router.push("/chat/index");
				} else {
					return false;
				}
			});
		},

		createUser(){
			let form = this.form;
			let userVo = {
				"id": util.uuid(),
				"userName": form.username,
				"head": form.head
			};
			store.dispatch('set_userVo', userVo);
			// let userVo1 = store.getters.get_userVo
			// console.log(userVo1);
		},

		initheadImgs(){
			let headImgs = this.headImgs;
			for (var index=1; index < 25; index++){ 
				let item = {
					label: require("../assets/head/head" + index + ".png"),
          value: "head" + index,
				}
				headImgs.push(item);
			}
			console.log(headImgs);
		},

		/**
		 * 回显头像并设置值
		 */
		showHeadImg(item){
			this.$refs["head"].$el.children[0].setAttribute(
				"style",
				"background-image: url(" +
					item.label +
					"); background-size: auto 38px;background-repeat: no-repeat;background-position: center;"
			);
			this.form.head = item.value;
			this.dialogVisible = false;
			//focus事件需要手动触发才能去掉提示
			this.$refs.loginForm.validateField('head');
		}


	},
	mounted() {
		this.initheadImgs();
	},
}
</script>

<style scoped>
  .login-box {
		background-color: white;
    border: 1px solid #DCDFE6;
    width: 350px;
    margin: 180px auto;
    padding: 35px 35px 15px 35px;
    border-radius: 5px;
    -webkit-border-radius: 5px;
    -moz-border-radius: 5px;
    box-shadow: 0 0 25px rgb(0 0 0 / 10%);
  }

  .login-title {
    text-align: center;
    margin: 0 auto 40px auto;
    color: #303133;
  }

	.panel-head{
		display: flex;
		flex-wrap: wrap;
		height: 400px;
    overflow-y: scroll;
	}

	.panel-head::-webkit-scrollbar {
		display: none;
	}

	.panel-head img{
		width: 100px; 
		height: 100px;
		margin: 10px;
	}


</style>