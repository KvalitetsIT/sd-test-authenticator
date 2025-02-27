console.log('Running SD select login!');

var setCookieVal = function(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
};
    

var selectLoginMethod = function(alias) {

    setCookieVal('selectlogin', alias, '1');

    // Reload	
    var searchParams = new URLSearchParams(window.location.search);
    window.location.search = searchParams.toString();    
};

var selectLoginMethodWithChannel = function(alias, channel) {

    setCookieVal('selectlogin', alias, '1');
    setCookieVal('sdchannel', channel, '1');

    // Reload	
    var searchParams = new URLSearchParams(window.location.search);
    window.location.search = searchParams.toString();    
};
