
$(document).ready(function(){
    $("#sim").click(function(){
        $(".active").attr("class","");
        $("#li-sim").attr("class","active");
        $(".page-header").text("Simulation Result");
    });

    $("#home").click(function(){
        $(".active").attr("class","");
        $("#li-home").attr("class","active");
        $(".page-header").text("Basic Data");
    });
    $("#one").click(function(){
        $("#aon_net").hide();
        $("ude_net").hide();
        $("#b_net").show();
        $("strong").text("Basic Abstract Road Network");
    });
    $("#two").click(function(){
        $("#b_net").hide();
        $("ude_net").hide();
        $("#aon_net").show();
        $("strong").text("All or Nothing Assignment");
        graphInit();

    });

    var timer;
    $("#three").click(function(){
        $("#b_net").hide();
        $("#aon_net").hide();
        $("#ude_net").show();
        $("label").show();

         timer = setInterval(function(){
            $("label").text("["+(++sec)+" s]");
        },1000);

        $("strong").text("Dynamic Equilibrium Assignment");
        udeInit();
    });

    $("#stop").click(function(){
        clearInterval(myVar);
        clearInterval(timer);
    });

    $("#one_b").click(function(){
        $("#display1").show();
        $("#display2").show();
        $("#display3").show();
        $("#display4").hide();
        $("#display5").hide();
        $("#display6").hide();
        $("#display7").hide();
        $("#display8").hide();
        $("#display12").hide();
        $("#display13").hide();

        $("#display9").hide();
        $("#display10").hide();
        $("#display11").hide();
        $("strong").text("Result of Dynamic Equilibrium");
    });
    $("#two_b").click(function(){
        $("#display1").hide();
        $("#display2").hide();
        $("#display3").hide();
        $("#display4").show();
        $("#display5").show();
        $("#display6").hide();
        $("#display7").hide();
        $("#display8").hide();
        $("#display12").hide();
        $("#display13").hide();

        $("#display9").hide();
        $("#display10").hide();
        $("#display11").hide();
        $("strong").text("Static self-Adjusting Pricing");
        graphInit_b();
        spInit();
    });

    $("#three_b").click(function(){
        $("#display1").hide();
        $("#display2").hide();
        $("#display3").hide();
        $("#display4").hide();
        $("#display5").hide();
        $("#display6").fadeTo( "slow", 1 );
        $("#display7").fadeTo( "slow", 1 );
        $("#display8").fadeTo( "slow", 1 );
        $("#display12").fadeTo( "slow", 1 );
        $("#display13").fadeTo( "slow", 1 );

        $("#display9").hide();
        $("#display10").hide();
        $("#display11").hide();
        $("strong").text("Equilibrium-based Pricing (Flows & Cost)");
    });

    $("#four_b").click(function(){
        $("#display1").hide();
        $("#display2").hide();
        $("#display3").hide();
        $("#display4").hide();
        $("#display5").hide();
        $("#display6").hide();
        $("#display7").hide();
        $("#display8").hide();
        $("#display12").hide();
        $("#display13").hide();

        $("#display9").show();
        $("#display10").show();
        $("#display11").show();
        $("strong").text("Equilibrium-based Pricing (Network)");
        ude_last_Init();
        vtp_last_Init();
        ftp_last_Init();
    });

});