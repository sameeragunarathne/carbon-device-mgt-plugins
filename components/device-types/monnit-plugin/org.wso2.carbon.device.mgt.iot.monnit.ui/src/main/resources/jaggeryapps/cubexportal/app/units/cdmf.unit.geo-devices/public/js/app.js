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
var applications = {
    "2" : "Temperature",
    "43" : "Humidity",
    "1" : "Voltage"
}

$(document).ready(function () {
    initialLoad(false);
    getToken();
    getDevicesList();
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

    showMarkersOnChange();

    map.zoomControl.setPosition('bottomright');

    // // add the search bar to the map
    // var controlSearch = new L.Control.Search({
    //     position:'topleft',
    //     layer: markersLayer,
    //     initial: false,
    //     zoom: 3,
    //     marker: false,
    //     textPlaceholder: 'search...'
    // });

    // map.addControl(controlSearch);

    var selectedMarker = L.AwesomeMarkers.icon({
        icon: ' ',
        markerColor: 'red'
    });
    var prev;
    map.on('click', function (e) {
        if(prev) {
            map.removeLayer(prev);
        }
        prev = L.marker(e.latlng, {
            icon: selectedMarker
        }).addTo(map);
        getLocationName(e.latlng);
        $.noty.closeAll();
    });

    map.on('zoomend', function () {
        currentZoomLevel = map.getZoom();
        setTimeout(showMarkesOnZoomEnd(currentZoomLevel),2000);
    });

    map.on('dragend',function(){
        showMarkersOnChange();
    });

    //setting the sidebar to be opened when page loads
    $("a[href='#left_side_pannel']").trigger('click');
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

var globalMarker;
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
            globalMarker = marker;
            // marker.openPopup();
           // $($(document).find(".leaflet-popup-content")).width('400px');
        });
    }
}

var devicePopupManagement= function(deviceName, deviceType, deviceIdentifier,deviceStatus,deviceOwner,lat,lng){

    var backEndUrl = '/monnit/1.0.0/monnit/devices/group?gatewayID=' + deviceIdentifier + '&deviceName=' + encodeURI(deviceName);

    invokerUtil.get(backEndUrl,function (result) {
        var deviceName = result;
        backEndUrl = '/monnit/1.0.0/monnit/devices?gatewayID=' + deviceIdentifier + '&deviceName=' + encodeURI(deviceName);
        invokerUtil.get(backEndUrl,function (result) {
            var obj = jQuery.parseJSON(result);
            console.log(obj);
            var deviceMgtUrl= "/cubexportal/device-details/"+'?id=' + deviceIdentifier + '&deviceName=' + deviceName + '&lat='+lat+'&lng='+lng+'';
            var cardContent = '<div id="location-marker-card" class="" style="width:100%"> ' +
                '<div class="card-block" style="width: 400px; padding-bottom: 5px"> <h4 style="display: inline">'+ obj[0].deviceGroup.name +'</h4> <span style="display: inline;float: right;margin-right: 10px"><a href="'+deviceMgtUrl+'" class="card-link">View Details</a></span> </div>' +
                '<span class="divider"></span><div class="card-block" style="width: 400px">';
            $.each(obj[0].devices, function(key, value) {
                if(key>0){
                    cardContent+='<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 "> <div class="well well-sm"> <h4>'+value.properties[4].value+'</h4> <p>'+ applications[""+value.properties[13].value+""] + ' <span style=" float:right;color: limegreen"><i class="fa fa-check"></i> Active</span></p> </div> </div>';
                    if(key%2==0) {
                        cardContent+='</div><div class="row">';
                    }
                } else {
                    cardContent+='<div class="row">';
                }
            });
            cardContent+='</div>' ;
                // +'<div class="card-block" style="width: 400px; padding-bottom: 5px"><span style="display: inline;float: right;margin-right: 10px;" style="color: red" onclick="removeDevice("'+obj[0].deviceGroup.name +'")">Remove</span></div>';
            var content = '<div id="location-marker-card" class="" style="width:100%"> ' +
                '<div class="card-block" style="width: 400px"> <h4 style="display: inline">Mt Saint Device</h4> <span style="display: inline;float: right;margin-right: 10px"><a href="#" class="card-link">Another link</a></span> </div>' +
                '<div class="card-block" style="width: 400px"> <div class="row"> <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 "> <div class="well well-sm"> <h4>300mV</h4> <p>Volt <span style=" float:right;color: limegreen"><i class="fa fa-check"></i> Active</span></p> </div> </div> ' +
                '<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"> <div class="well well-sm"> <h4>30C</h4> <p>Temp <span style=" float:right;color: limegreen"><i class="fa fa-check"></i> Active</span></p> </div> </div> </div> '
            // '<div class="row"> <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"> <div class="well well-sm"> <h4>+2%</h4> <p>Humidity</p> </div> </div> <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"> <div class="well well-sm"> <h4>+2%</h4> <p>Humidity</p> </div> </div> </div> </div> </div>';
            var content2 = '<div class="card-block" style="width: 400px"> <h4 style="display: inline">Mt Saint Device</h4> <span style="display: inline;float: right;margin-right: 10px"><a href="#" class="card-link">Another link</a></span> </div>';
            popupContent = cardContent;
            globalMarker.bindPopup(popupContent,{
                maxWidth: 500});
            globalMarker.openPopup();
            $($(document).find(".leaflet-popup-content")).width('400px');
            var metrix = $(document).find(".leaflet-popup.leaflet-zoom-animated").css('transform');
            var temp = metrix.replace("matrix(", "");
            var offset = 100;
            var res = temp.split(",");
            var transform = parseInt(res[4]) - offset;
            metrix = metrix.replace(res[4],transform+"");
            $(document).find(".leaflet-popup.leaflet-zoom-animated").css('transform', metrix);
        },function (error) {
            console.log("error when calling backend api to retrieve geo clusters");
            console.log(error);
        });
    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });

};

var removeDevice = function (device) {
    console.log(device);
}

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
    var lat = deviceJsonObject.deviceInfo.location.latitude;
    var lng = deviceJsonObject.deviceInfo.location.longitude;
    devicePopupManagement(deviceName,deviceType,deviceIdentifier,deviceStatus,deviceOwner, lat, lng);
};

var loadEnrollDeviceForm = function () {
    $("#device-form-div").fadeIn();
    $("#enroll-device-header").fadeIn();
    $("#device-map-wrapper").removeClass("col-md-12");
    $("#device-map-wrapper").removeClass("col-lg-12");
    $("#device-map-wrapper").removeClass("col-sm-12");
    $("#device-map-wrapper").removeClass("col-xs-12");
    $("#device-map-wrapper").addClass("col-md-10");
    $("#device-map-wrapper").addClass("col-lg-10");
    $("#device-map-wrapper").addClass("col-sm-10");
    $("#device-map-wrapper").addClass("col-xs-10");
    getSensorList();
    getGatewayList();
};

var hideEnrollDeviceForm = function () {
    $("#enroll-device-header").hide();
    $("#device-form-div").fadeOut();
    $("#device-map-wrapper").removeClass("col-md-10");
    $("#device-map-wrapper").removeClass("col-lg-10");
    $("#device-map-wrapper").removeClass("col-sm-10");
    $("#device-map-wrapper").removeClass("col-xs-10");
    $("#device-map-wrapper").addClass("col-md-12");
    $("#device-map-wrapper").addClass("col-lg-12");
    $("#device-map-wrapper").addClass("col-sm-12");
    $("#device-map-wrapper").addClass("col-xs-12");
    $('#sensors').find('option').remove();
    $('#gateways').find('option').remove();
};

var getLocationName = function (latlng) {
    var lat = Math.round(latlng.lat * 100000) / 100000;
    var lng = Math.round(latlng.lng * 100000) / 100000;
    $("#device-location-input").attr('data-latitude',latlng.lat);
    $("#device-location-input").attr('data-longitude',latlng.lng);
    var url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat="+lat+"&lon="+lng;
    $.ajax({
        url: url,
        type : 'GET',
        success : function (result) {
            $("#device-location-input").val(result.display_name);
        }, error : function () {
            console.log("error occurred retrieving location name")
        }
    });
};

var getToken = function () {
    token = $('#nav-bar-span').attr('data-token');
    if($("#nav-bar-span").attr('data-logged-in') != "true") {
        loadInitialDevices();
    }
};

var saveDevice = function () {
    var backEndUrl = '/monnit/1.0.0/monnit/devices';
    var device = {
        "gatewayID": $("#gateways").val(),
        "deviceName": $("#device-name").val(),
        "sensorIds" : $("#sensors").val(),
        "location" : {
            "latitude" : $("#device-location-input").attr("data-latitude"),
            "longitude" : $("#device-location-input").attr("data-longitude")
        }
    };
    invokerUtil.post(backEndUrl, device, function (result) {
        console.log(result);
        $("#modalDevice").modal('show');
        hideEnrollDeviceForm();
    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
        $("#modalDevice").modal('show');
        hideEnrollDeviceForm();
    });
};

var getSensorList = function () {
    var backEndUrl = '/monnit/1.0.0/monnit/sensors?token=' + token;
    invokerUtil.get(backEndUrl,function (result) {
        var obj = jQuery.parseJSON(result);
        $.each(obj, function(key, value) {
            $("#sensors").append( $("<option>")
                .val(value.SensorID)
                .html(value.SensorID + ' - ' +value.SensorName)
            );
        });

    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });
};

var getGatewayList = function () {
    var backEndUrl = '/monnit/1.0.0/monnit/gateways?token=' + token;
    invokerUtil.get(backEndUrl,function (result) {
        var obj = jQuery.parseJSON(result);
        $.each(obj, function(key, value) {
            $("#gateways").append( $("<option>")
                .val(value.GatewayID)
                .html(value.GatewayID + ' - ' +value.Name)
            );
        });

    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });
};

var loadInitialDevices = function () {
    var backEndUrl = '/monnit/1.0.0/monnit/init?token=' + token;
    invokerUtil.get(backEndUrl,function (result) {
        console.log(result);
    },function (error) {
        console.log("error when calling backend api to retrieve geo clusters");
        console.log(error);
    });
};


var deviceList=[];
var getDevicesList = function () {
    var backEndUrl = '/monnit/1.0.0/monnit/devices?gatewayID=0&deviceName=';
    invokerUtil.get(backEndUrl, function (result) {
        var obj = jQuery.parseJSON(result);
        // console.log(obj);
        $.each(obj, function(key, value) {
            var deviceGrpName = value.deviceGroup.name;
            var deviceGrpId = value.deviceGroup.id;
            if(deviceGrpName!="BYOD" && deviceGrpName!="COPE") {
                $('#searchresults').append('<option style="font-weight: normal">'+deviceGrpName+'</option>');
                if(value.devices[0]){
                    if(value.devices[0].deviceInfo.location) {
                            var location = value.devices[0].deviceInfo.location;
                            var device = {
                                "id": deviceGrpId,
                                "name" : deviceGrpName,
                                "location" : location
                            };
                            deviceList.push(device);
                    }
                }
            }
        });
        document.getElementById('search-input').addEventListener('input', function () {
            var val = $('#search-input').val();
            var result = $.grep(deviceList, function(e){ return e.name == val; });
            console.log(result);
            var corner1 = L.latLng(result[0].location.latitude, result[0].location.longitude),
                corner2 = L.latLng(result[0].location.latitude, result[0].location.longitude),
                bounds = L.latLngBounds(corner1, corner2);
            map.fitBounds(bounds);
        });
        // var search = document.querySelector('#search-input');
        // var results = document.querySelector('#searchresults');
        //
        // search.addEventListener('keyup', function handler(event) {
        //     var templateContent = document.querySelector('#resultstemplate').content;
        //     while (results.children.length) results.removeChild(results.firstChild);
        //     var inputVal = new RegExp(search.value.trim(), 'i');
        //     var clonedOptions = templateContent.cloneNode(true);
        //     var set = Array.prototype.reduce.call(clonedOptions.children, function searchFilter(frag, el) {
        //         if (inputVal.test(el.textContent) && frag.children.length < 5) frag.appendChild(el);
        //         return frag;
        //     }, document.createDocumentFragment());
        //     results.appendChild(set);
        // });
    },function (error) {
        console.log("error occured");
    });
};

