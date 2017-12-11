/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var token; 

$(document).ready(function () {
    token = $('#data-holder').attr('data-token');
    initialLoad(false);
    getDeviceGrp();
});

/**
 * App.js
 */
$(".modal").draggable({
    handle: ".modal-header"
});

//Clear modal content for reuse the wrapper by other functions
$('body').on('hidden.bs.modal', '.modal', function () {
    $(this).removeData('bs.modal');
});

/*Map layer configurations*/
var map;
var geoClusters;
var markersLayer = new L.LayerGroup();
var popupContent;

var zoomLevel = 15;
var tileSet = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
var attribution = "&copy; <a href='https://openstreetmap.org/copyright'>OpenStreetMap</a> contributors";

function initialLoad() {
    if (document.getElementById('map') == null) {
        setTimeout(initialLoad, 500); // give everything some time to render
    } else {
        initializeMap();
        $("#loading").hide();
    }
}

function initializeMap() {
    if (typeof(map) !== 'undefined') {
        map.remove();
    }
    if (document.getElementById('map') == null) {
        console.log("no map");
    } else {
    }
    map = L.map("map", {
        zoom: 3,
        center: [0, 0],
        layers: [defaultOSM],
        zoomControl: true,
        attributionControl: false,
        maxZoom: 20,
        maxNativeZoom: 18
    });
    L.tileLayer(tileSet, {attribution: attribution}).addTo(map);
    markersLayer.addTo(map);

    // showMarkersOnChange();

    map.zoomControl.setPosition('bottomright');

    var lat = $("#data-holder").attr("data-lat");
    var lng = $("#data-holder").attr("data-lng");

    var selectedMarker = L.AwesomeMarkers.icon({
        icon: ' ',
        markerColor: 'blue'
    });

    var pos = {};
    pos.lat = lat;
    pos.lng = lng;
    L.marker(pos, {
        icon: selectedMarker
    }).addTo(map);

    map.on('click', function (e) {
        $.noty.closeAll();
    });

    map.on('zoomend', function () {
        // currentZoomLevel = map.getZoom();
        // setTimeout(showMarkesOnZoomEnd(currentZoomLevel),2000);
    });

    map.on('dragend',function(){
        // showMarkersOnChange();
    });
    //setting the sidebar to be opened when page loads
    $("a[href='#left_side_pannel']").trigger('click');
    var zoom = 10;
    map.setView([pos.lat, pos.lng], zoom);
    // centerLeafletMapOnMarker(map, pos);
}

function centerLeafletMapOnMarker(map, latLngs) {
    var markerBounds = L.latLngBounds([latLngs.lat, latLngs.lng]);
    map.fitBounds(markerBounds);
}

var showMarkesOnZoomEnd = function (zoomLevel) {
     if(map.getZoom()===zoomLevel){
         showMarkersOnChange();
     }
};
var showMarkersOnChange=function(){
    var bounds = map.getBounds();
    var maxLat = bounds._northEast.lat;
    var maxLong = bounds._northEast.lng;
    var minLat = bounds._southWest.lat;
    var minLong = bounds._southWest.lng;
    var zoom = map.getZoom();
    var backEndUrl = '/api/device-mgt/v1.0/geo-services/1.0.0/stats/deviceLocations'+
        '?'+'&minLat='+minLat+'&maxLat='+maxLat+'&minLong='+minLong+
        '&maxLong='+maxLong+'&zoom='+zoom;
    markersLayer.clearLayers();
    invokerUtil.get(backEndUrl,successCallBackRectangles,function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });
}


var successCallBackRectangles = function (clusters) {
    geoClusters=clusters;
    geoGridControl();
};


/* Geo-Grid control*/
function geoGridControl(){

    var geoClustersJsonObject=JSON.parse(geoClusters);
    for (var key in geoClustersJsonObject) {
        if (geoClustersJsonObject.hasOwnProperty(key)) {
            var cluster = geoClustersJsonObject[key];
            var count= parseInt(cluster.count);
            geoClusterMarker(count,cluster.coordinates.latitude,cluster.coordinates.longitude,
                cluster.southWestBound.latitude,
                    cluster.northEastBound.latitude,cluster.southWestBound.longitude,cluster.northEastBound.longitude,
                cluster.deviceIdentification,cluster.deviceType);

        }
    }
}

function geoClusterMarker(count, clusterLat, clusterLong, minLat, maxLat, minLong, maxLong,deviceIdentification,
                          deviceType) {
    var deviceMarker = L.AwesomeMarkers.icon({
        icon: ' ',
        markerColor: 'blue'
    });
    var rectangle_details = {count: count,minLat: minLat-0.001, maxLat: maxLat+0.001, minLong: minLong-0.001,
        maxLong: maxLong+0.001,deviceIdentification:deviceIdentification,deviceType:deviceType};
    var event_capture = function (extra_data,marker) {
        return function (event) {
            // event and extra_data will be available here
            handleMarkerEvents(event,extra_data,marker);
        };
    };
    if(count == 1) {
        var marker = L.marker([clusterLat, clusterLong], {
            icon: deviceMarker
        });
        marker.addEventListener("mouseover",event_capture(rectangle_details,marker));
    }else{
        var marker = L.marker([clusterLat, clusterLong], {
            icon: L.divIcon({
                iconSize: [30, 30], html: count
            })
        }).bindPopup(null);
    }
    marker.addEventListener("click",event_capture(rectangle_details,marker));
    marker.addTo(markersLayer);
}

function handleMarkerEvents(event,extra_data,marker) {
    if(event.type==="click") {
        var southWestCorner = L.latLng(extra_data.minLat, extra_data.minLong);
        var northEastCorner = L.latLng(extra_data.maxLat, extra_data.maxLong);
        var rectangleBounds = L.latLngBounds(southWestCorner, northEastCorner);
        map.fitBounds(rectangleBounds);
    }else if (event.type=="mouseover"){
        var deviceType = extra_data.deviceType;
        var deviceIdentification = extra_data.deviceIdentification;
        devicePopupContentBackEndCall(deviceType,deviceIdentification,marker,function () {
            marker.bindPopup(popupContent);
            marker.openPopup();
        });
    }

}

var devicePopupManagement= function(deviceName, deviceType, deviceIdentifier,deviceStatus,deviceOwner){

    var deviceMgtUrl= "/devicemgt/device/";
    var html1='<div>';
    var html2 = '<p><h3>'+'<a href="' + deviceMgtUrl +deviceType+'?id='+deviceIdentifier+ '" target="_blank">' + deviceName + '</a>'+'</h3></p>' ;
    var html3 = '<p>'+'Type : '+ deviceType+'</p>';
    var html4 = '<p>'+'Status : '+deviceStatus+'</p>';
    var html5 = '<p>'+ 'Owner : ' + deviceOwner + '</p>';
    var html6='</div>';
    var html=html1+html2+html3+html4+html5+html6;
    return html;
};

var devicePopupContentBackEndCall = function(type,deviceIdentification,marker,callback){
    var popupContentBackEndUrl='/api/device-mgt/v1.0/devices/1.0.0/'+type+'/'+deviceIdentification;
    invokerUtil.get(popupContentBackEndUrl,successCallBackDeviceDetails,function (error) {
        console.log("error when calling backend api to retrive device data");
        console.log(error);
    });
    callback();
}

var successCallBackDeviceDetails=function(device){
    var deviceJsonObject = JSON.parse(device);
    var deviceName = deviceJsonObject.name;
    var deviceType = deviceJsonObject.type;
    var deviceIdentifier = deviceJsonObject.deviceIdentifier;
    var deviceStatus = deviceJsonObject.enrolmentInfo.status;
    var deviceOwner = deviceJsonObject.enrolmentInfo.owner;
    popupContent = devicePopupManagement(deviceName,deviceType,deviceIdentifier,deviceStatus,deviceOwner);

};

var getDeviceGrp = function () {
    var gatewayId = $("#data-holder").attr("data-gatewayID");
    var deviceName = $("#data-holder").attr("data-device-name");
    var backEndUrl = '/monnit/1.0.0/monnit/devices?gatewayID=' + gatewayId + '&deviceName=' + encodeURI(deviceName);
    invokerUtil.get(backEndUrl,function (result) {
        var obj = jQuery.parseJSON(result);
        var lastCommunicated;
        var status = obj[0].devices[0].enrolmentInfo.status;
        $.each(obj[0].devices[0].properties, function(key, value) {
            if(value.name === 'LastCommunicationDate') {
                lastCommunicated = value.value;
            }
        });
        populateGatewayData(status, lastCommunicated);
        populateSensorData(obj[0].devices);
    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });
};

var loadMap = function () {
    var gatewayId = $("#data-holder").attr("data-gatewayID");
    var lat = $("#data-holder").attr("data-lat");
    var lng = $("#data-holder").attr("data-lng");

};

var populateGatewayData = function (status, lastCommunicated) {
    var content = '<tr role="row"> <td class="sorting_1" style="padding:10px 15px;">Status</td> <td class="sorting_1">'+status+'</td> </tr>'
        + '<tr role="row"> <td class="sorting_1" style="padding:10px 15px;">Last Communicated</td> <td class="sorting_1">'+lastCommunicated+'</td> </tr>';
    $("#gateway-table tbody").append(content);
};

var populateSensorData = function (devices) {
    console.log(devices);
    var dataArr = [];
    var deviceIds = [];
    var nameArr = [];
    var checkDigitArr = [];
    var readings = [];
    var alertStatus = [];
    var batteryLevel = [];
    var signalStrength = [];
    var application = [];
    dataArr.push(nameArr);
    dataArr.push(readings);
    dataArr.push(checkDigitArr);
    dataArr.push(signalStrength);
    dataArr.push(batteryLevel);
    $.each(devices, function(key, value) {

        if(key > 0) {
            deviceIds.push(devices[key].deviceIdentifier);
            nameArr.push(value.name);
            $.each(value.properties, function(key, value) {
                if(value.name === 'CurrentReading') {
                    var currentReading = value.value;
                    if(currentReading === "No Reading Available") {
                        currentReading = "N/A";
                    }
                    readings.push(currentReading);
                } else if(value.name === 'AlertsActive') {
                    alertStatus.push(value.value);
                } else if(value.name === 'SignalStrength') {
                    signalStrength.push(value.value);
                } else if(value.name === 'BatteryLevel') {
                    batteryLevel.push(value.value);
                } else if(value.name === 'checkDigit') {
                    checkDigitArr.push(value.value);
                } else if(value.name === 'MonnitApplicationID') {
                    application.push(value.value);
                }
            });
        }
    });
    var html = '';
    $.each(nameArr, function(key, value) {
        var signalBar;
        var batteryBar;

        if(signalStrength[key]>50) {
            signalBar = 'color:green';
        } else {
            signalBar = 'color:red';
        }

        if(batteryLevel[key]>50) {
            batteryBar = 'color:green';
        } else {
            batteryBar = 'color:red';
        }

        var imgElement;
        if(application[key]==2) {
            imgElement = '<img style="padding-left: 10px" src="/cubexportal/public/cdmf.unit.geo-devices-map/img/rsz_temp1.png">';
        }
        
        if(application[key]==43) {
            imgElement = '<img style="padding-left: 10px" src="/cubexportal/public/cdmf.unit.geo-devices-map/img/rsz_humidity.png">';
        }

        html+= '<tr role="row" data-device-id="'+deviceIds[key]+'" data-device-name="' + nameArr[key] + '" onclick="tableCellAction(this)">'+
        '<td style="width: 10%; padding-left: 30px;border: none">' + imgElement +'</td>'+
        '<td style="border: none">'+ nameArr[key]+'</td>'+
        '<td class="sorting_1" style="padding:10px 15px;border: none">'+ readings[key]+'</td>'+
        '<td class="sorting_1" style="padding:10px 15px;border: none">'+ checkDigitArr[key]+'</td>'+
        '<td class="sorting_1" style="padding:10px 15px;border: none"><p><span class="fw fw-wifi fw-1x" style="' + signalBar + '">&nbsp;'+ signalStrength[key]+'</span></p></td>'+
        '<td class="sorting_1" style="padding:10px 15px;border: none"><p><span class="fw fw-battery" style="' + batteryBar + '">&nbsp;'+ batteryLevel[key]+'</span></p></td>'+
        '<td class="sorting_1" style="width: 7%;border: none"><p><span class="fw fw-sort-down fw-1x"></span></p></td></tr>';
    });
    $('#sensor-data-table tbody').append(html);
};
var prev;
var tableCellAction = function(obj) {
    var deviceId = $(obj).attr("data-device-id");
    var deviceName = $(obj).attr("data-device-name");
    var span = $(obj).find('span')[2];
    if(prev == deviceId) {
        $("#analytics-section").toggle();
        if($(span).hasClass('fw-sort-down')) {
            $(span).removeClass('fw-sort-down');
            $(span).addClass('fw-sort-up');
        } else {
            $(span).removeClass('fw-sort-up');
            $(span).addClass('fw-sort-down');
        }
    } else {
        $("#analytics-section").slideDown();
        $(span).removeClass('fw-sort-down');
        $(span).addClass('fw-sort-up');
    }

    var to = new Date().getTime()/1000;
    var from  = new Date(new Date().getTime() - 1 * 60 * 60 * 1000).getTime()/1000;
    drawGraph_monnit(Math.round(from), Math.round(to), deviceId, deviceName);
    //TODO use token method
    getNotifications(token, 10, deviceId);
    prev = deviceId;
};

var getNotifications = function (token, minutes, sensorID) {
    var backEndUrl = '/monnit/1.0.0/monnit/recent-notifications?token=' + token + '&minutes=' + minutes + '&sensorID=' + sensorID;
    invokerUtil.get(backEndUrl,function (result) {
        var obj = jQuery.parseJSON(result);
        console.log(obj);
        $('#notification-table tbody').empty();
        var html = '';
        $.each(obj, function(key, value) {
            console.log(value.content);
            if(value.content){
                html += '<tr role="row"> ' +
                '<td class="sorting_1" style="border: none;width: 1%;"> <span class="wr-hidden-operations-icon fw-stack" style="display: inline;float: left; padding-top: 15px"> <i class="fw fw-warning fw-stack-2x" style="color: darkorange"></i> </span> <span class="card-body" style="display: inline;float: left;"><h4 class="card-title">'+value.content+'</h4><p class="card-text" style="color:#ccc">' + value.notificationDate + '</p></span> </td> ' +
                '</tr>';
            }
        });
        $('#notification-table tbody').append(html);
    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });
};
