import { ref, computed } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import router from "@/router";

export const usePieceMakeStore = defineStore("piecemake", () => {
  // =========== STATE ===============

  const selectedLayout = ref("");
  const backImage = ref("");

  // =========== GETTER ===============

  const getSelectedLayout = computed(() => {
    return selectedLayout.value;
  });
  const getBackImage = computed(() => {
    return backImage.value;
  });

  // =========== ACTION ===============
  function resetPieceMakeValue() {
    backImage.value = '';
    selectedLayout.value = '';
  }

  const makeImage = function (image) {
    axios({
      url: `${import.meta.env.VITE_REST_PIECE_API}/make`,
      method: "POST",
      data: image,
    })
      .then((res) => {
        backImage.value = res.data.data;
      })
      .catch((err) => {
        alert("이미지 생성 중 오류가 발생했습니다. 다시 시작해주세요.");
        router.push("/piece");
      });
  };

  return {
    // state
    selectedLayout,
    backImage,
    // getter
    getSelectedLayout,
    getBackImage,
    // action
    makeImage,
    resetPieceMakeValue,
  };
});
