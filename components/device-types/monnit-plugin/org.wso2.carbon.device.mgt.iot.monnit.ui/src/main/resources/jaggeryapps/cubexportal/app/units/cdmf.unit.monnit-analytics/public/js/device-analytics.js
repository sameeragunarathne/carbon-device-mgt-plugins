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
var sensorType1 = "light";
var sensorType2 = "humidity";
var sensorType3 = "temperature";
var sensorType4 = "Sensor Connectivity";
var sensorType5 = "Signal Strength";
var sensorType6 = "Battery Status";
var sensorType1Graph;
var sensorType2Graph;
var sensorType3Graph;
var sensorType4Graph;
var sensorType5Graph;
var sensorType6Graph;

function drawGraph_monnit(from, to) {
    clearGraph(1);
    clearGraph(2);
    clearGraph(3);
    clearGraph(4);
    clearGraph(5);
    clearGraph(6);
    var sensors = getDevices($("#details").attr('data-gatewayName'));
    console.log(sensors);
    var devices;
    var tzOffset = 0;//new Date().getTimezoneOffset() * 60;
    var chartWrapperElmId = "#chartDivSensorType1";
    var graphWidth = $(chartWrapperElmId).width() - 50;
    var graphConfigSensorType1 = getGraphConfig("chartSensorType1");
    var graphConfigSensorType2 = getGraphConfig("chartSensorType2");
    var graphConfigSensorType3 = getGraphConfig("chartSensorType3");
    var graphConfigSensorType4 = getGraphConfig("chartSensorType4");
    var graphConfigSensorType5 = getGraphConfig("chartSensorType5");
    var graphConfigSensorType6 = getGraphConfig("chartSensorType6");

    function clearGraph(id) {
        $("#sensorType" + id + "yAxis").html("");
        $("#smoother-sensorType" + id).html("");
        $("#sensorType" + id + "Legend").html("");
        $("#chartSensorType" + id).html("");
        $("#sensorType" + id + "xAxis").html("");
        $("#sensorType" + id + "Slider").html("");
    }

    function getDevices(gatewayName) {
        var backEndUrl = '/monnit/1.0.0/monnit/devices?deviceName=' + gatewayName;
        console.log(backEndUrl);
        var sensors = [];
        invokerUtil.get(backEndUrl,function (result) {
            var obj = jQuery.parseJSON(result);
            $.each(obj[0].devices, function(key, value) {
                if(key > 0) {
                    var deviceIdentifier = value.deviceIdentifier;
                    var name = value.name;
                    $.each(obj[0].devices[key].properties, function(key, value) {
                        if(value.name === 'MonnitApplicationID') {
                            var sensor = {
                                "id" : deviceIdentifier,
                                "applicationId" : value.value,
                                "name" : name
                            };
                            sensors.push(sensor);
                        }
                    });
                }
            });
            successCallback();
        },function (error) {
            console.log("error when calling backend api to retrieve geo clusters");
            console.log(error);
        });
        return sensors;
    }

    function getGraphConfig(placeHolder) {
        return {
            element: document.getElementById(placeHolder),
            width: graphWidth,
            height: 400,
            strokeWidth: 2,
            renderer: 'line',
            interpolation: "linear",
            unstack: true,
            stack: false,
            xScale: d3.time.scale(),
            padding: {top: 0.2, left: 0.02, right: 0.02, bottom: 0.2},
            series: []
        }
    };


    function successCallback() {
        if (sensors) {
            console.log(sensors.length);
            for (var i = 0; i < sensors.length; i++) {
                //temperature sensor
                if(sensors[i].applicationId == 2) {
                    $('#chartDivSensorType1').show();
                    $('#sensorType1Title').html('<strong>' + sensors[i].name + ' Sensor</strong>');
                    graphConfigSensorType1['series'].push(
                    {
                        'color': palette.color(),
                        'data': [{
                            x: parseInt(new Date().getTime() / 1000),
                            y: 0
                        }],
                        'name': sensors[i].name
                    });
                }
                //humidity sensor
                if(sensors[i].applicationId == 43) {
                    $('#chartDivSensorType2').show();
                    $('#sensorType2Title').html('<strong>' + sensors[i].name + ' Sensor</strong>');
                    graphConfigSensorType2['series'].push(
                        {
                            'color': palette.color(),
                            'data': [{
                                x: parseInt(new Date().getTime() / 1000),
                                y: 0
                            }],
                            'name': sensors[i].name
                        });
                }
                //voltage sensor
                if(sensors[i].applicationId == 1) {
                    $('#chartDivSensorType3').show();
                    $('#sensorType3Title').html('<strong>' + sensors[i].name + 'Sensor</strong>');
                    graphConfigSensorType3['series'].push(
                        {
                            'color': palette.color(),
                            'data': [{
                                x: parseInt(new Date().getTime() / 1000),
                                y: 0
                            }],
                            'name': sensors[i].name
                        });
                }
                graphConfigSensorType4['series'].push(
                    {
                        'color': palette.color(),
                        'data': [{
                            x: parseInt(new Date().getTime() / 1000),
                            y: 0
                        }],
                        'name': sensors[i].name
                    });
                graphConfigSensorType5['series'].push(
                    {
                        'color': palette.color(),
                        'data': [{
                            x: parseInt(new Date().getTime() / 1000),
                            y: 0
                        }],
                        'name': sensors[i].name
                    });
                graphConfigSensorType6['series'].push(
                    {
                        'color': palette.color(),
                        'data': [{
                            x: parseInt(new Date().getTime() / 1000),
                            y: 0
                        }],
                        'name': sensors[i].name
                    });
            }
        } else {
            graphConfigSensorType1['series'].push(
                {
                    'color': palette.color(),
                    'data': [{
                        x: parseInt(new Date().getTime() / 1000),
                        y: 0
                    }],
                    'name': $("#details").data("devicename")
                });
            graphConfigSensorType2['series'].push(
                {
                    'color': palette.color(),
                    'data': [{
                        x: parseInt(new Date().getTime() / 1000),
                        y: 0
                    }],
                    'name': $("#details").data("devicename")
                });
            graphConfigSensorType3['series'].push(
                {
                    'color': palette.color(),
                    'data': [{
                        x: parseInt(new Date().getTime() / 1000),
                        y: 0
                    }],
                    'name': $("#details").data("devicename")
                });
            graphConfigSensorType4['series'].push(
                {
                    'color': palette.color(),
                    'data': [{
                        x: parseInt(new Date().getTime() / 1000),
                        y: 0
                    }],
                    'name': $("#details").data("devicename")
                });
            graphConfigSensorType5['series'].push(
                {
                    'color': palette.color(),
                    'data': [{
                        x: parseInt(new Date().getTime() / 1000),
                        y: 0
                    }],
                    'name': $("#details").data("devicename")
                });
            graphConfigSensorType6['series'].push(
                {
                    'color': palette.color(),
                    'data': [{
                        x: parseInt(new Date().getTime() / 1000),
                        y: 0
                    }],
                    'name': $("#details").data("devicename")
                });
        }

        sensorType1Graph = new Rickshaw.Graph(graphConfigSensorType1);
        sensorType2Graph = new Rickshaw.Graph(graphConfigSensorType2);
        sensorType3Graph = new Rickshaw.Graph(graphConfigSensorType3);
        sensorType4Graph = new Rickshaw.Graph(graphConfigSensorType4);
        sensorType5Graph = new Rickshaw.Graph(graphConfigSensorType5);
        sensorType6Graph = new Rickshaw.Graph(graphConfigSensorType6);
        drawGraph(sensorType1Graph, "sensorType1yAxis", "sensorType1Slider", "sensorType1Legend", sensorType1
            , graphConfigSensorType1, "chartSensorType1");
        drawGraph(sensorType2Graph, "sensorType2yAxis", "sensorType2Slider", "sensorType2Legend", sensorType2
            , graphConfigSensorType2, "chartSensorType2");
        drawGraph(sensorType3Graph, "sensorType3yAxis", "sensorType3Slider", "sensorType3Legend", sensorType3
            , graphConfigSensorType3, "chartSensorType3");
        drawGraph(sensorType4Graph, "sensorType4yAxis", "sensorType4Slider", "sensorType4Legend", sensorType4
            , graphConfigSensorType4, "chartSensorType4");
        drawGraph(sensorType5Graph, "sensorType5yAxis", "sensorType5Slider", "sensorType5Legend", sensorType5
            , graphConfigSensorType5, "chartSensorType5");
        drawGraph(sensorType6Graph, "sensorType6yAxis", "sensorType6Slider", "sensorType6Legend", sensorType6
            , graphConfigSensorType6, "chartSensorType6");

        function drawGraph(graph, yAxis, slider, legend, sensorType, graphConfig, chart) {
            graph.render();
            var xAxis = new Rickshaw.Graph.Axis.Time({
                graph: graph
            });
            xAxis.render();
            var yAxis = new Rickshaw.Graph.Axis.Y({
                graph: graph,
                orientation: 'left',
                element: document.getElementById(yAxis),
                width: 40,
                height: 410
            });
            yAxis.render();
            var slider = new Rickshaw.Graph.RangeSlider.Preview({
                graph: graph,
                element: document.getElementById(slider)
            });
            var legend = new Rickshaw.Graph.Legend({
                graph: graph,
                element: document.getElementById(legend)
            });
            var hoverDetail = new Rickshaw.Graph.HoverDetail({
                graph: graph,
                formatter: function (series, x, y) {
                    var date = '<span class="date">' +
                        moment.unix((x + tzOffset)).format('Do MMM YYYY h:mm:ss a') + '</span>';
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
            getData(graphConfig, graph, chart);
        }

        function getData(graphConfig, graph, chart) {
            var backendApiUrl;
            var param;
            var placeHolder = $("#details").attr('data-backend-api-url');
            var gatewayId = $("#details").attr('data-gatewayId');
            if(chart === 'chartSensorType1') {
                backendApiUrl = placeHolder
                + "?from=" + from + "&to=" + to + "&applicationId=2&gatewayId=" + gatewayId;
                param = "dataValue";
            } else if(chart === 'chartSensorType2') {
                backendApiUrl = placeHolder
                + "?from=" + from + "&to=" + to + "&applicationId=43&gatewayId=" + gatewayId;
                param = "dataValue";
            } else if(chart === 'chartSensorType3') {
                backendApiUrl = placeHolder
                + "?from=" + from + "&to=" + to + "&applicationId=1&gatewayId=" + gatewayId;
                param = "dataValue";
            } else if(chart === 'chartSensorType4'){
                backendApiUrl = placeHolder
                + "?from=" + from + "&to=" + to + "&gatewayId=" + gatewayId;
                param = "state";
            } else if(chart === 'chartSensorType5'){
                backendApiUrl = placeHolder
                + "?from=" + from + "&to=" + to + "&gatewayId=" + gatewayId;
                param = "sensorSignalStrength";
            } else if(chart === 'chartSensorType6'){
                backendApiUrl = placeHolder
                + "?from=" + from + "&to=" + to + "&gatewayId=" + gatewayId;
                param = "sensorBatteryLevel";
            }

            var successCallbackFunc = function (data) {
                if (data) {
                    if(data != 'null') {
                        drawLineGraph(JSON.parse(data),graphConfig, graph, param);
                    }
                };
            };
            invokerUtil.get(backendApiUrl, successCallbackFunc, function (message) {
                console.log(message);
            });
        }

        function drawLineGraph(data,graphConfig, graph, param) {
            var chartData1 = [];
            var chartData2 = [];
            if(data) {
                for (var i = 0; i < data.length; i++) {
                    if(data[i].values.applicationID == 2) {
                        chartData1.push(
                            {
                                x: parseInt(data[i].values.messageDate),
                                y: parseInt(data[i].values[param])
                            }
                        );
                    }
                    if(data[i].values.applicationID == 43) {
                        chartData2.push(
                            {
                                x: parseInt(data[i].values.messageDate),
                                y: parseInt(data[i].values[param])
                            }
                        );
                    }
                }
            }
            // graphConfigSensorType1['series'].pop();
            if(chartData1.length > 0) {
                graphConfig.series[0].data = chartData1;
            } else if(chartData2.length > 0) {
                if(graphConfig.series[1].data){
                    graphConfig.series[1].data = chartData2;
                }
            }
            if(chartData2.length > 0) {
                graphConfig.series[1].data = chartData2;
            }
            graph.update();
        }
    }

}


