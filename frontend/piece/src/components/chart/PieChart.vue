<template>
    <div>
        <canvas ref="pieChartCanvas"></canvas>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import Chart from "chart.js/auto";
import { defineProps } from 'vue';

const props = defineProps({
    chartData: Object
});

const pieChartCanvas = ref(null);
let pieChartInstance = null;

const initOrUpdateChart = () => {
    const ctx = pieChartCanvas.value.getContext('2d');
    if (pieChartInstance) {
        pieChartInstance.data = props.chartData;
        pieChartInstance.update();
    } else {
        pieChartInstance = new Chart(ctx, {
            type: 'pie',
            data: props.chartData,
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        enabled: true,      
                        backgroundColor: 'rgba(0, 0, 0, 0.7)',  
                        titleFont: {
                            family: 'Regular', 
                            size: 14,
                            weight: 'bold',
                            style: 'normal'
                        },
                        bodyFont: {
                            family: 'Regular',  
                            size: 12,
                            weight: 'normal',
                            style: 'normal'
                        },
                        footerFont: {
                            family: 'Regular', 
                            size: 10,
                            weight: 'normal',
                            style: 'normal'
                        },
                        cornerRadius: 4, 
                        xPadding: 10,      
                        yPadding: 10,      
                        displayColors: true  
                    }
                },
                hover: {
                    mode: 'index',
                    intersect: true,
                    onHover: function(event, chartElement) {
                        if (chartElement.length) {
                            event.native.target.style.cursor = 'pointer';
                        } else {
                            event.native.target.style.cursor = 'default';
                        }
                    }
                }
            }
        });
    }
};

onMounted(() => {
    initOrUpdateChart();
});

watch(() => props.chartData, initOrUpdateChart, { deep: true });

onUnmounted(() => {
    if (pieChartInstance) {
        pieChartInstance.destroy();
    }
});
</script>
