<template>
  <div>
		<div class="user-info">
			<div class="user-info-top">
				<div style="margin-top:30px">
					<img :src="require('../../assets/head/' + userVo.head + '.png')">
				</div>
				<div class="user-name">
					{{userVo.userName}}
					<el-button @click="quit()" type="primary">退出</el-button>
				</div>
			</div>
			<div style="height:400px">

			</div>
		</div>
    <div class="interface" v-drag>
      <div class="container">
        <div class="title">
          {{currentUser.userName}}
        </div>
        <div class="content">
          <div class="chat-list">
            <div style="margin-top:4px">
							<div class="chat-person" v-for="(item, index) in userList" v-show="index < 7" :key="index"
								:class="{
									'chat-person-hover':index == hoverIndex,
									'chat-person-active':item.id == currentUser.id
								}"
								@click="doActive(item)"
								@mouseover="hoverIndex = index"
								@mouseout="hoverIndex = -1"							
							>
								<div style="30%">
									<img :src="item.head">
								</div>
								<div class="person-name">
									{{item.userName}}
								</div>
								<div class="message-remind">
									<span v-if="item.unreadNum > 0"
										:style="{'border-radius':(item.unreadNum > 9 ? '20px':'50%'),
										'padding': (item.unreadNum > 9 ? '0 3px':'0px')
										}"
										>
										{{item.unreadNum >99 ? '99+': item.unreadNum}}	
									</span>
								</div>
							</div>
            </div>
          </div>
          <div class="chat-panel">
            <div class="chat-record" ref="chatRecord">
              <template v-for="(item,index) in currentChatRecord">
								<template v-if="userVo.id == item.userId">
									<div class="record-item" style="justify-content: end;" v-bind:key="index" >
										<div>
											<div style="display: flex;padding-top: 20px;">
												<div class="right-record-content">
													{{item.content}}
												</div>
												<div class="right-triangle"></div>
											</div>
										</div>
										<div>
												<img :src="item.head">
										</div>
									</div>
								</template>
								<template v-else>
									<div class="record-item" v-bind:key="index" >
										<div>
												<img :src="item.head">
										</div>
										<div>
											<div class="record-username">
												{{item.userName}}
											</div>
											<div style="display: flex;">
												<div class="left-triangle"></div>
												<div class="left-record-content">
													{{item.content}}
												</div>
											</div>
										</div>
									</div>
								</template>
                
              </template>
            </div>
            <div class="send-panel">
              <div style="padding-top:20px">
                <textarea v-model="content" @keyup.alt.83="sendContent"></textarea>
              </div>
              <div class="send-bottom">
                <el-button style="width: 100px;" @click="sendContent()" type="primary">发送(S)</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import util from '@/utils/util';
import config from '@/utils/config';
import store from '@/store/index';
import constant from '@/utils/constant';

export default {
  data () {
    return {
      util,
			config,
      store,
			constant,
			socket: null,
			userList: [
				{
					id: 'ALL',
					userName: '群聊',
					head: require('../../assets/head/group.png'),
					channelId: 'ALL',
					chatRecord: [],
					unreadNum: 0
				}
			],
			hoverIndex: -1,
			currentUser: {},
			content: '',
			recordMap: {},
			currentChatRecord: [],
    }
  },
	//自定义指令
	directives: {
		drag: {
			// 指令的定义
			bind: function(el) {
				let oDiv = el;  // 获取当前元素
				
				oDiv.onmousedown = (e) => {
					//点击底部拖动
					if(e.toElement.className != 'title'){
						return;
					}
					// 算出鼠标相对元素的位置
					let disX = e.clientX - oDiv.offsetLeft;
					let disY = e.clientY - oDiv.offsetTop;
					document.onmousemove = (e) => {
						// 用鼠标的位置减去鼠标相对元素的位置，得到元素的位置
						let left = e.clientX - disX;
						let top = e.clientY - disY;
						oDiv.style.left = left + 'px';
						oDiv.style.top = top + 'px';
					};
					document.onmouseup = function () {
						document.onmousemove = null
						document.onmouseup = null
				}
				}
			}
		}
  },
  methods: {

		sendMessage(data){
			this.socket.send(JSON.stringify(data));
		},

		init: function () {
			if(typeof(WebSocket) === "undefined"){
					alert("您的浏览器不支持socket")
			}else{
				// 实例化socket
				this.socket = new WebSocket("ws://" + config.WEB_SOCKET_URL);
				// 监听socket连接
				this.socket.onopen = this.open
				// 监听socket错误信息
				this.socket.onerror = this.error
				// 监听socket消息
				this.socket.onmessage = this.getMessage
			}
		},
		open: function () {
			console.log("socket连接成功");
			this.registerUser();
		},
		error: function () {
				console.log("连接错误")
		},

		getMessage: function (msg) {
			let data = JSON.parse(msg.data);
			switch(data.type) {
				case constant.RECEIVE.ADD:
					this.addUser(data.data);
					break;
				case constant.RECEIVE.REMOVE:
					this.removeUser(data.data);
					break;	
				case constant.RECEIVE.CHAT:
					this.loadChatRecord(data.data);
					break;
				default:
			}
		},

		close: function () {
				console.log("socket已经关闭")
		},

		/**
		 * 连接成功后注册用户信息
		 */
		registerUser(){
			let userVo = this.userVo;
			let data = {
				id: userVo.id,
				userName: userVo.userName,
				head: userVo.head
			}
			let param = {
				data: data,
				type: constant.SEND.USER_REGISTER,
			}
			this.sendMessage(param);
		},

		addUser(data){
			let userVo = store.getters.get_userVo;
			if(data.id == userVo.id){
				return;
			}
			let userList = this.userList;
			data.head = require('../../assets/head/' + data.head + '.png');
			data.chatRecord = [];
			data.unreadNum = 0;
			userList.push(data);
		},

		removeUser(data){
			var index = this.userList.findIndex(item => item.id == data.id);
			this.userList.splice(index,1)
		},

		/**
		 * 用户选择
		 */
		doActive(user){
			user.unreadNum = 0;
			this.currentUser = user;
			this.currentChatRecord = this.currentUser.chatRecord;
			this.scrollBottom();
		},

		/**
		 * 发送内容
		 */
		sendContent(){
			let currentUser = this.currentUser;
			if(!currentUser.channelId){
				return;
			}
			if(this.content == ''){
				return;
			}
			let data = {
				targetChannelId: currentUser.channelId,
				content: this.content
			};
			let param = {
				data: data,
				type: constant.SEND.CHAT,
			}
			this.sendMessage(param);
			this.content = '';
		},

		/**
		 * 聊天滚动条到底部
		 */
		scrollBottom(){
			this.$nextTick(() =>{
				this.$refs.chatRecord.scrollTop = this.$refs.chatRecord.scrollHeight;
			})
		},

		/**
		 * 加载聊天记录
		 */
		loadChatRecord(data){
			let userVo = data.userVo;
			let record = {
				userId: userVo.id,
				userName: userVo.userName,
				head: require('../../assets/head/' + userVo.head + '.png'),
				content: data.content,
			}
			let user = this.userList.filter(item => item.channelId == data.sourceChannelId)[0];
			let currentUser = this.currentUser;
			//添加未读消息标识
			if(currentUser && currentUser.id != user.id){
				user.unreadNum = user.unreadNum + 1;
			}
			//内容过多清除
			if(user.chatRecord.length > 120){
				user.chatRecord = user.chatRecord.slice(30, user.chatRecord.length - 1);
			}
			user.chatRecord.push(record);
			this.scrollBottom();
		},

		quit(){
			this.$router.replace({ path:'/', });
		}

  },
  computed: {
    userVo () {
      return this.$store.getters.get_userVo
    }
  },

	mounted () {
		this.init();
	},

	destroyed() {
		//离开路由之后断开websocket连接
    this.socket.close();
  },

}
</script>

<style scoped>
.interface {
	background-color: white;
	position: absolute;
	text-align: center;
	left: 25%;
  width: 900px;
  height: 715px;
  -moz-border-radius: 5px;
  box-shadow: 0 0 25px rgb(0 0 0 / 10%);
  border-radius: 3px;
	top: 50px;
}
.title {
  border-radius: 3px 3px 0 0;
  background-image: linear-gradient(to right, #009dff, #00ccff);
  font-size: 30px;
  color: white;
  height: 50px;
  line-height: 50px;
}
.content {
  height: 665px;
  display: flex;
}
.chat-list {
  border-right: 2px solid #f2f2f2;
  width: 25%;
}
.chat-list {
  border-right: 2px solid #f2f2f2;
  width: 25%;
}
.chat-panel {
  width: 75%;
	position:relative;
}
.chat-person {
  display: flex;
  align-items: center;
  padding: 20px 10px 20px 20px;
	margin-right: 3px;
}
.chat-person-hover{
	background-color: #aaaaaa21;
}
.chat-person-active{
	border-left: 5px solid #409eff;
	background-color: #e4e4e4;
}

.chat-person img{
	border-radius: 50%; 
	width: 50px;
	height: 50px;
}
.person-name{
	width: 60%;
	text-align: left;
	margin-left: 10px;
	font-size: 24px;
}

.message-remind{
	width: 10%;
}

.message-remind{
	background-color: red;
	color: white;
	border-radius: 20px;
	font-size: 14px;
}

/**聊天记录 */
.chat-record {
  position: absolute;
  top: 0px;
  height: 400px;
	width: 100%;
  overflow: auto;
}

.chat-record::-webkit-scrollbar {
  width: 10px;
}

.chat-record::-webkit-scrollbar-track {
  background-color: #e4e7ed;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}

.chat-record::-webkit-scrollbar-thumb {
  background-color: #c0c4cc;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}

.record-item {
	display: flex;
	padding: 10px;
}

.record-item img{
	border-radius: 50%; 
	width: 40px;
	height: 40px;
}
.left-triangle{
	width:0;
	height:0;
	border-top: -6px solid transparent;
	border-bottom:20px solid transparent;
	border-right:8px solid #f2f2f2;
}

.right-triangle{
	width:0;
	height:0;
	border-top:-6px solid transparent;
	border-bottom:20px solid transparent;
	border-left:8px solid #009dff;
}

.record-username{
	margin-left: 10px;
	font-size: 20px;
	color:#606266;
	text-align: start;
}
.left-record-content{
	background-color: #f2f2f2;
	border-radius: 0px 5px 5px 5px;
	padding: 10px;
}

.right-record-content{
	color: white;
	background-color: #009dff;
	border-radius: 5px 0px 5px 5px;
	padding: 10px;
}

/**聊天记录 */

/**发送模块 */
.send-panel{
	position:absolute; 
	bottom:0px;
	width: 100%;
	border-top: 2px solid #f2f2f2;
	height:250px
}
.send-panel textarea {
  font-size: 24px;
  width: 95%;
  height: 150px;
  background: transparent;
  border-style: none;
  border: 0;
  outline: none;
  resize: none;
}
.send-bottom{
	padding: 10px 20px 20px 0;
	text-align: end;
}
/**发送模块 */

.user-info{
	width: 340px;
	position: absolute;
	top: 62px;
	left: 80%;
	height: 200px;
	background-image: linear-gradient(to right, #009dff, #00ccff);
	border-radius: 5px;
}
.user-info img{
	width: 100px;
	height: 100px;
	border-radius: 50px;
}
.user-name{
	color: white;
	margin-top: 20px;
	font-size: 24px;
	font-weight: 600;margin-left: 10px;
}
.user-info-top{
	height:150px;
	display: flex;
	align-items: center;
	justify-content: center;
}
</style>
