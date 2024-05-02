<template>
    <!-- header -->
    <h1>내 조각 리스트 버전</h1>
    <RouterLink :to="{ name: 'pieceCalendar' }">캘린더</RouterLink>
    
    <!-- filter -->
    <div class="piecelistmyview-scroll-container">
        <div class="piecelistmyview-tab-navigation">
            <div class="piecelistmyview-tab-menu" ref="tabMenu">
                <FilterItem
                    v-for="(item, index) in filterItems"
                    class="piecelistmyview-tab-btn"
                    :key="index"
                    :labelType="item.labelType"
                    :title="item.title"
                    :isSelect="item.isSelect"
                    @click="handleItemSelectClick(index)"
                ></FilterItem>
            </div>
        </div>
    </div>

    <!-- list -->
    <div class="piecelistmyview-list-container">
        <div class="piecelistmyview-list-grid">
            <div v-for="(item, index) in filteredMyList" :key="index" class="piecelistmyview-list-item">
            <ListImageItem
                :pieceId="item.pieceId"
                :performanceType="item.performanceType"
                :frontImg="item.frontImg"
                :title="item.title"
                @click="handleItemClick"
            ></ListImageItem>
        </div>
        </div>
        
    </div>

    
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { usePiecelistStore } from "@/stores/piecelist";
import { useRouter } from "vue-router";
import FilterItem from "@/components/item/FilterItem.vue";
import ListImageItem from "@/components/item/ListImageItem.vue";

const store = usePiecelistStore();

const piecelistMyList = computed(() => store.getPiecelistMyList);
const filteredMyList = computed(() => computedFilteredMyList());
const selectedOption = ref("ALL");

function computedFilteredMyList() {
    if (selectedOption.value === "ALL") {
        return piecelistMyList.value;
    } else if (selectedOption.value === "MOVIE") {
        return piecelistMyList.value.filter((item) => item.performanceType === "MOVIE");
    } else if (selectedOption.value === "THEATER") {
        return piecelistMyList.value.filter((item) => item.performanceType === "THEATER");
    } else if (selectedOption.value === "MUSICAL") {
        return piecelistMyList.value.filter((item) => item.performanceType === "MUSICAL");
    } else if (selectedOption.value === "CONCERT") {
        return piecelistMyList.value.filter((item) => item.performanceType === "CONCERT");
    } else if (selectedOption.value === "ETC") {
        return piecelistMyList.value.filter((item) => item.performanceType === "ETC");
    } else {
        return [];
    }
}

const handleItemClick = () => {
    alert("선택");
};


const filterItems = ref([
    {
        labelType: "ALL",
        title: "전체",
        isSelect: true,
    },
    {
        labelType: "MOVIE",
        title: "영화",
        isSelect: false,
    },
    {
        labelType: "THEATER",
        title: "연극",
        isSelect: false,
    },
    {
        labelType: "MUSICAL",
        title: "뮤지컬",
        isSelect: false,
    },
    {
        labelType: "CONCERT",
        title: "콘서트",
        isSelect: false,
    },
    {
        labelType: "ETC",
        title: "기타",
        isSelect: false,
    },
]);

const handleItemSelectClick = (index) => {
    for (let item of filterItems.value) {
        if (item.isSelect) {
            item.isSelect = false;
        }
    }
    filterItems.value[index].isSelect = !filterItems.value[index].isSelect;
    selectedOption.value = filterItems.value[index].labelType;
};

// filter
const tabMenu = ref(null);
const activeDrag = ref(false);

const handleMouseMove = (event) => {
    if (!activeDrag.value) return;
    tabMenu.value.scrollLeft -= event.movementX;
    tabMenu.value.classList.add("dragging");
};

const handleMouseUp = () => {
    activeDrag.value = false;
    tabMenu.value.classList.remove("dragging");
};

const handleMouseDown = () => {
    activeDrag.value = true;
};

onMounted(async () => {
    await store.findPiecelistMyList();
    tabMenu.value.addEventListener("mousedown", handleMouseDown);
    document.addEventListener("mouseup", handleMouseUp);
    tabMenu.value.addEventListener("mousemove", handleMouseMove);
});

onBeforeUnmount(() => {
    tabMenu.value.removeEventListener("mousedown", handleMouseDown);
    document.removeEventListener("mouseup", handleMouseUp);
    tabMenu.value.removeEventListener("mousemove", handleMouseMove);
});

</script>

<style>

/* filter */
.piecelistmyview-scroll-container {
    position: relative;
    /* width: 450px; */
    transition: 0.5s ease;
    margin-bottom: 2rem;
}

.piecelistmyview-tab-navigation {
    position: relative;
    max-width: fit-content;
    margin: 0 auto;
    display: flex;
    justify-content: center;
    align-items: center;
}

.piecelistmyview-tab-menu {
    list-style: none;
    white-space: nowrap;
    overflow-x: auto;
    user-select: none;
    scroll-behavior: smooth;
}

.piecelistmyview-tab-menu.dragging {
    scroll-behavior: unset;
    cursor: grab;
}

.piecelistmyview-tab-menu::-webkit-scrollbar {
    display: none;
}

.piecelistmyview-tab-btn {
    display: inline-block;
    margin: 0 0.2rem;
    cursor: pointer;
    user-select: none;
    transition: 0.3s ease;
}

.piecelistmyview-tab-menu.dragging .tab-btn {
    pointer-events: none;
}

/* list */
.piecelistmyview-list-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(8rem, 1fr));
    justify-items: center; 
    grid-gap: 1rem;
}

.piecelistmyview-list-item {
    width: auto; 
}

@media (min-width: 1024px) {
    .piecelistmyview-list-grid {
        grid-template-columns: repeat(4, minmax(16rem, 5fr));
    }
}
</style>