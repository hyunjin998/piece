<template>
  <div>
    <!-- image -->
    <img class="cakedetailview-image-image" :src="data.posterImageUrl" />

    <!-- heart -->
    <div class="cakedetailview-icon-container">
      <div class="cakedetailview-heart-container">
        <font-awesome-icon
          class="cakedetailview-heart-icon"
          :icon="cakeHeartState ? ['fas', 'heart'] : ['far', 'heart']"
          style="color: var(--main-color)"
          @click="handleHeartClick"
        />
        <div class="cakedetailview-heart-message">
          {{ cakeHeartCount }}명이 찜하고 있어요
        </div>
      </div>
    </div>

    <hr />

    <!-- title -->
    <div class="cakedetailview-item-container">
      <div
        class="cakedetailview-item-type"
        :class="{
          'movie-background': cultureType === 'MOVIE',
          'theater-background': cultureType === 'THEATER',
          'musical-background': cultureType === 'MUSICAL',
          'concert-background': cultureType === 'CONCERT',
          'other-background':
            cultureType !== 'MOVIE' &&
            cultureType !== 'THEATER' &&
            cultureType !== 'MUSICAL' &&
            cultureType !== 'CONCERT',
        }"
      >
        <p v-if="cultureType === 'MOVIE'">영화</p>
        <p v-else-if="cultureType === 'THEATER'">연극</p>
        <p v-else-if="cultureType === 'MUSICAL'">뮤지컬</p>
        <p v-else-if="cultureType === 'CONCERT'">콘서트</p>
        <p v-else>기타</p>
      </div>
    </div>
    <div class="cakedetailview-item-title">{{ data.title }}</div>

    <!-- content -->
    <div class="cakedetailview-content-container">
      <div class="cakedetailview-content-content">{{ data.overview }}</div>
      <div v-if="data.runtime!=0" class="cakedetailview-content-runtime">
        <strong>상영 시간</strong> <br />{{ data.runtime }}
      </div>
      <br />
      <div
        v-if="data.castList && data.castList.length > 0"
        class="cakedetailview-content-cast"
      >
        <strong>출연진</strong> <br />{{ data.castList.join(", ") }}
      </div>
    </div>

    <hr />

    <!-- chat -->
    <div class="cakedetailview-chat-container">
      <div class="cakedetailview-chat-title">채팅방</div>
      <div class="cakedetailview-chat-container">
        <ChatItem
          v-for="(item, index) in cakeChatList"
          class="cakedetailview-chat-item"
          :key="index"
          :chatRoomId="item.chatRoomId"
          :senderLabel="item.title"
          :senderNickname="item.nickname"
          :senderImg="item.profileImage"
          :content="item.content"
          :createdAt="item.createdAt"
        ></ChatItem>

        <NoItem
          class="cakedetailview-chat-noitem"
          v-if="isCakeChatList"
          :content="'아직 대화를 나누지 않은 채팅방이예요'"
        >
        </NoItem>
      </div>
    </div>
    <!-- button -->
    <RoundButton
      class="cakedetailview-button"
      :roundButtonContent="'채팅 참여하기'"
      :roundButtonFunction="handleParticipateClick"
      :isRoundDisable="true"
    ></RoundButton>
  </div>
  <!-- chat modal -->
  <YesNoModal
    v-if="showChatParticipateModal"
    :modalTitle="'오픈채팅에 참여하시겠어요?'"
    :modalContent="'참여중인 사람들과 토픽에 대한 대화를 나눌 수 있어요.'"
    :buttonSuccess="'네'"
    :buttonFail="'아니요'"
    :handleFailClick="handleFailClick"
    :handleSuccessClick="handleSuccessClick"
  ></YesNoModal>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useCommonStore } from "@/stores/common";
import { useCakeDetailStore } from "@/stores/cakedetail";
import { useUserStore } from "@/stores/user";
import { useChatRoomStore } from "@/stores/chatroom";
import { useWebSocketStore } from "@/stores/websocket";
import ChatItem from "@/components/chat/ChatItem.vue";
import RoundButton from "@/components/button/RoundButton.vue";
import NoItem from "@/components/item/NoItem.vue";
import YesNoModal from "@/components/modal/YesNoModal.vue";

const commonStore = useCommonStore();
const cakeDetailStore = useCakeDetailStore();
const userStore = useUserStore();
const chatRoomStore = useChatRoomStore();
const webSocketStore = useWebSocketStore();

const route = useRoute();
const router = useRouter();

const concertId = route.params.concertId;
const cultureId = route.params.cultureId;

const data = ref({
  posterImageUrl: "",
  heartCnt: 0,
  title: "",
  overview: "",
  runtime: "",
  castList: [],
});

const chatRoomInfo = ref({});

const cakeHeartState = ref(false);
const cakeChatList = computed(() => cakeDetailStore.getCakeChatList);
const isCakeChatList = computed(() => cakeChatList.value == undefined);
const cakeHeartCount = computed(() => cakeDetailStore.getCakeHeartCount);

const cultureType = computed(() => cakeDetailStore.getCakeCultureType);

// chat modal
const showChatParticipateModal = ref(false);

const handleParticipateClick = () => {
  showChatParticipateModal.value = true;
};
const handleFailClick = () => {
  showChatParticipateModal.value = false;
};

const handleSuccessClick = () => {
  handleChatParticipate();
  showChatParticipateModal.value = false;
};

watch(cakeHeartCount, (newCount) => {
  data.value.heartCnt = newCount;
});

const handleHeartClick = async () => {
  const newHeartState = !cakeHeartState.value;

  try {
    if (newHeartState) {
      await cakeDetailStore.toggleHeart(data.value.cultureId);
    } else {
      await cakeDetailStore.removeHeart(data.value.cultureId);
    }
    cakeHeartState.value = newHeartState;
    userStore.setHeartState(data.value.cultureId, newHeartState);
    await cakeDetailStore.fetchHeartCount(data.value.cultureId);
    triggerBounce();
  } catch (error) {
    console.error("Failed to toggle heart", error);
  }
};

const triggerBounce = () => {
  const heartIcon = document.querySelector(".cakedetailview-heart-icon");
  heartIcon.classList.add("bounce");
  setTimeout(() => {
    heartIcon.classList.remove("bounce");
  }, 1000);
};

const handleChatParticipate = async () => {
  try {
    console.log("cultureId?:" + cultureId);

    // 오픈채팅 생성 or 중복 처리
    await cakeDetailStore.findCultureId(cultureId);

    await cakeDetailStore.joinChatRoom();

    await chatRoomStore.setChatRoomId(cakeDetailStore.getChatRoomId);
    console.log("현재 채팅방 번호를 갱신:" + chatRoomStore.getChatRoomId);
    await chatRoomStore.setIsPersonal(false);

    console.log("ispersonal: open 처리:" + chatRoomStore.getIsPersonal);

    // 사용자가 참가한 채팅방 정보 갱신
    await chatRoomStore.getChatRoomList(chatRoomStore.getIsPersonal);

    await chatRoomStore.getOpenChatRoomInfo(chatRoomStore.getChatRoomId);

    console.log(
      "케이크채팅방 정보?:" + JSON.stringify(chatRoomStore.getChatRoom)
    );

    chatRoomInfo.value = chatRoomStore.getChatRoom;

    // commonstore status 변경
    commonStore.headerType = "header6";
    commonStore.headerChatName = chatRoomInfo.value.culture.title;
    commonStore.headerChatImg = chatRoomInfo.value.culture.imageUrl;
    commonStore.headerChatCount = chatRoomInfo.value.participantCount;

    router.push("/chat");
  } catch (error) {
    console.error("Failed to join chat room", error);
  }
};

onMounted(async () => {
  commonStore.headerTitle = "케이크 상세보기";
  commonStore.headerType = "header2";

  // stompclient 생성
  await webSocketStore.stompConnect();

  if (!concertId || !cultureId) {
    console.error("Missing required parameters");
    return;
  }
  if (cultureType.value === "MOVIE") {
    await cakeDetailStore.fetchTmdbDetail(concertId);
  } else {
    await cakeDetailStore.fetchConcertCakeDetail(concertId);
  }

  await cakeDetailStore.fetchHeartCount(cultureId);

  data.value = {
    ...cakeDetailStore.cakeDetail,
  };
  cakeHeartState.value =
    userStore.getHeartState(cultureId) || cakeDetailStore.cakeDetail.isHearted;

  // 채팅 조회
  await cakeDetailStore.findCultureId(cultureId);
});
</script>

<style>
@keyframes bounce {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-30px);
  }
  60% {
    transform: translateY(-15px);
  }
}

.bounce {
  animation: bounce 1s;
}

/* image */
.cakedetailview-image-image {
  width: 100%;
  height: auto;
  object-fit: contain;
  user-select: none;
}

/* heart */
.cakedetailview-icon-container {
  display: flex;
  flex-direction: column;
  margin: 2rem 0 2rem 0;
  user-select: none;
}

.cakedetailview-heart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: 0.6rem 2rem 0 2rem;
  margin-bottom: 0.6rem;
}

.cakedetailview-heart-icon {
  width: 1.2rem;
  height: 1.2rem;
  margin-bottom: 0.6rem;
}

.cakedetailview-heart-icon:hover {
  cursor: pointer;
}

.cakedetailview-heart-message {
  display: flex;
  justify-self: center;
  align-self: center;
  text-align: center;
  margin-bottom: 0.6rem;
  font-family: "Regular";
  font-size: 1rem;
  color: var(--gray2-color);
}

/* title */
.cakedetailview-item-container {
  display: flex;
  justify-self: center;
  align-items: center;
  flex-direction: row;
  margin: 2rem 0rem 0.4rem 0rem;
  user-select: none;
}

.cakedetailview-item-type {
  font-family: "Semi";
  font-size: 1rem;
  color: var(--white-color);
  user-select: none;
  display: inline-block;
  line-height: 0.1rem;
  padding: 0rem 0.4rem;
  border-radius: 0.5rem;
  background-color: var(--main-color);
}

.cakedetailview-item-title {
  font-family: "Semi";
  font-size: 1.2rem;
  color: var(--black-color);
}

/* content */
.cakedetailview-content-container {
  margin: 2rem 0 2rem 0;
  user-select: none;
}

.cakedetailview-content-content {
  font-family: "Regular";
  font-size: 1rem;
  color: var(--black-color);
}

.cakedetailview-content-runtime {
  font-family: "Regular";
  font-size: 1rem;
  color: var(--black-color);
  margin-top: 0.5rem;
}

.cakedetailview-content-cast {
  font-family: "Regular";
  font-size: 1rem;
  color: var(--black-color);
  margin-top: 0.5rem;
}

/* chat */
.cakedetailview-chat-container {
  margin: 2rem 0 2rem 0;
  user-select: none;
}

.cakedetailview-chat-title {
  font-family: "Bold";
  font-size: 1.2rem;
  color: var(--gray2-color);
  margin-bottom: 1rem;
}

/* button */
.cakedetailview-button {
  margin: 2rem 0 2rem 0;
}

.cakedetailview-chat-noitem {
  margin-top: 5rem;
  margin-bottom: 5rem;
}
</style>
