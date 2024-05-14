<template>
    <div>
        <canvas
            ref="canvas"
            :style="{ width: '100%', maxWidth: '600px' }"
        ></canvas>
        <hr style="margin-top: 20px; margin-bottom: 20px" />
        <div class="chart-labels">
            <div
                v-for="(label, index) in data.labels"
                :key="index"
                class="label-item"
            >
                <span
                    class="color-box"
                    :style="{
                        backgroundColor:
                            data.datasets[0].backgroundColor[index],
                    }"
                ></span>
                <span class="label-text">{{ label }}</span>
                <span class="label-count"
                    >{{ data.datasets[0].data[index] }} 회</span
                >
            </div>
        </div>
    </div>
</template>

<script>
import Chart from "chart.js/auto";

export default {
    name: "PieChart",
    props: {
        data: Object,
    },
    mounted() {
        this.createChart();
    },
    methods: {
        createChart() {
            const ctx = this.$refs.canvas.getContext("2d");
            new Chart(ctx, {
                type: "pie",
                data: this.data,
                options: {
                    plugins: {
                        legend: {
                            display: false,
                        },
                    },
                    responsive: true,
                    title: {
                        display: false,
                    },
                },
            });
        },
    },
};
</script>

<style>
.chart-labels {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 10px;
}

.label-item {
    display: flex;
    align-items: center;
    width: 100%;
    justify-content: space-between;
    margin-bottom: 10px;
}

.color-box {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    margin-right: 10px;
}

.label-text {
    flex-grow: 1;
    text-align: left;
    font-family: "Medium";
    font-size: 1rem;
}

.label-count {
    white-space: nowrap;
    font-family: "Regular";
    font-size: 1rem;
}
</style>