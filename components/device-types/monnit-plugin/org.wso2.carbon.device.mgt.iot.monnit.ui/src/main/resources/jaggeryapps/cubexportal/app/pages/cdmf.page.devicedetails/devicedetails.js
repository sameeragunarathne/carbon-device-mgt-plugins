function onRequest(context) {
    var constants = require("/app/modules/constants.js");
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];
    var gatewayId = request.getParameter("id");
    var deviceName = request.getParameter("deviceName");
    var lat = request.getParameter("lat");
    var lng = request.getParameter("lng");

    var deviceViewData = {};
    if(gatewayId && deviceName) {
        deviceViewData["isValid"] = true;
        deviceViewData["gatewayID"] = gatewayId;
        deviceViewData["deviceName"] = deviceName;
        deviceViewData["lat"] = lat;
        deviceViewData["lng"] = lng;
        deviceViewData["token"] = devicemgtProps["Monnit"]["token"];
    }
    return deviceViewData;
}