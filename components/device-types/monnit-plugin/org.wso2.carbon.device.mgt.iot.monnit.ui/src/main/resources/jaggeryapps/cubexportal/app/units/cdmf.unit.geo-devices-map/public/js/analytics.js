/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var palette = new Rickshaw.Color.Palette({scheme: "classic9"});
var deviceID;
var deviceName;
function drawGraph_monnit(from, to, deviceId, devicename) {
    if(deviceId && devicename) {
        deviceID = deviceId;
        deviceName = devicename;   
    } 
    $("#y_axis-temperature").html("");
    $("#smoother-temperature").html("");
    $("#legend-temperature").html("");
    $("#chart-temperature").html("");
    $("#x_axis-temperature").html("");
    // $("#slider-temperature").html("");

    var devices = $("#monnit-div-chart").data("devices");
    var tzOffset = new Date().getTimezoneOffset() * 60;

    var chartWrapperElmId = "#monnit-div-chart";
    var graphWidth = $(chartWrapperElmId).width() - 50;
    var graphConfig = {
        element: document.getElementById("chart-temperature"),
        width: graphWidth,
        height: 400,
        strokeWidth: 2,
        renderer: 'line',
        interpolation: "basis",
        unstack: true,
        stack: false,
        xScale: d3.time.scale(),
        padding: {top: 0.2, left: 0.02, right: 0.02, bottom: 0.2},
        series: []
    };

    graphConfig['series'].push(
        {
            'color': palette.color(),
            'data': [{
                x: parseInt(new Date().getTime() / 1000),
                y: 0
            }],
            'name': deviceName
        });

    var graph = new Rickshaw.Graph(graphConfig);

    graph.render();

    var xAxis = new Rickshaw.Graph.Axis.Time({
        graph: graph
    });

    xAxis.render();

    var yAxis = new Rickshaw.Graph.Axis.Y({
        graph: graph,
        orientation: 'left',
        element: document.getElementById("y_axis-temperature"),
        width: 40,
        height: 410
    });

    yAxis.render();

    var legend = new Rickshaw.Graph.Legend({
        graph: graph,
        element: document.getElementById('legend-temperature')
    });

    var hoverDetail = new Rickshaw.Graph.HoverDetail({
        graph: graph,
        formatter: function (series, x, y) {
            var date = '<span class="date">' +
                moment((x + tzOffset) * 1000).format('Do MMM YYYY h:mm:ss a') + '</span>';
            var swatch = '<span class="detail_swatch" style="background-color: ' +
                series.color + '"></span>';
            return swatch + series.name + ": " + parseInt(y) + '<br>' + date;
        }
    });

    var shelving = new Rickshaw.Graph.Behavior.Series.Toggle({
        graph: graph,
        legend: legend
    });

    var order = new Rickshaw.Graph.Behavior.Series.Order({
        graph: graph,
        legend: legend
    });

    var highlighter = new Rickshaw.Graph.Behavior.Series.Highlight({
        graph: graph,
        legend: legend
    });

    getData();

    function getData() {
        var backEndUrl = '/monnit/1.0.0/monnit/sensor/stats?deviceId=' + deviceID + '&from=' + from +'&to='+ to;
        console.log(deviceID);
        invokerUtil.get(backEndUrl,function (result) {
            var data = jQuery.parseJSON(result);
            console.log(data);
            drawLineGraph(data);
        },function (error) {
            console.log("error when calling backend api to retrieve geo clusters");
            console.log(error);
        });
    }

    function drawLineGraph(data) {
        var chartData = [];
        if(data) {
            for (var i = 0; i < data.length; i++) {
                chartData.push(
                    {
                        x: parseInt(data[i].values.messageDate),
                        y: parseInt(data[i].values.dataValue)
                    }
                );
            }
        }
        graphConfig.series[0].data = chartData;
        graph.update();
    }
}
