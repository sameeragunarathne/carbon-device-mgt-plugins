function onRequest(context) {
    var log = new Log("cdmf.unit.geo-devices-map/geo-devices-map.js");
    var constants = require("/app/modules/constants.js");
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];

    var deviceViewData = {};

    deviceViewData["isValid"] = true;
    deviceViewData["isLogged"] = session.get("logged_flag");
    deviceViewData["token"] = devicemgtProps["Monnit"]["token"];
    session.put("logged_flag", "true");
    
    return deviceViewData;
}