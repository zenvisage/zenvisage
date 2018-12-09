app.controller('fileuploadController', [
      '$scope', '$rootScope', '$cookies', 'datasetInfo',
      function($scope, $rootScope, $cookies, datasetInfo){

  var formData;
  var datasetNameInput;
  var checker = '';
  var attributes_global;
  var no_login_required= document.getElementById("loginmodaltrigger").style.display=="none"
  $("#upload_dataset_btn").on("click",function(e){
    // check if signout button displayed, if so then already signed in
    var not_logged_in = document.getElementById("signoutbutton").style.display=="none" 
    if (!no_login_required){
      if (not_logged_in){
         alert("Please log in first to upload dataset.");
         $('#uploaderModal').modal('toggle'); // close uploader
         $("#loginmodaltrigger").click() // open up login screen
      }  
    }
  })
    
  $('#uploaderForm').on('submit', function(e) {
    e.preventDefault();

    formData = new FormData(this);
    if ($("#dataInputTextbox").val()==null){
      if (formData.get("csv").name.split(".").pop() != "csv" ){
        alert("Please select a csv file or copy and paste data in the textbox below");
        return;
      }
    }
    
    if (no_login_required) {   // if login is not available (configured to be off), allow uploads
      uploadDataset()
    } else if ($cookies.getObject('userinfo')) {  // user is logged in
      var username = $cookies.getObject('userinfo')['username'][0];
      var file_size = {
        "size": formData.get("csv").size.toString(),
      };
      
      if (username != 'root') {
        $.ajax({
                type: "POST",
                url: "/zv/file_size_checker",
                data: file_size,
                async: false,
                success: function(response){
                  callback_for_file_checker(response);
                },
                error: function() {
                  alert("failed!!");
                } 

        });
        if(checker == "true"){
          return;
        }
      }
      // if (formData.get("csv").size > 1 && username != 'root') {
      //   alert("Do not upload files over 100MB");
      //   return;
      // }
      uploadDataset();
    } else {
      alert("Please log in first to upload dataset.");
    }
  });

  function callback_for_file_checker(response){
    if(response){
      alert("Do not upload files over 100MB");
      checker = "true";
    }
  }

  // uploadDataset uploads the global formData variable
  function uploadDataset() {
    var textBoxVal = $("#dataInputTextbox").val();
    var uploadedData = formData.get("csv"); 
    if (textBoxVal!=""){
      uploadedData = textBoxVal
    } 
    Papa.parse(uploadedData, {
      complete: inferDtype
    });
    
    datasetNameInput = $("#datasetNameInput").val();
    console.log('test:',$(this).attr('action'),$(this).attr('method'));
    log.info("dataset upload: ",$("#datasetNameInput").val())
    // $('#uploaderModal').modal('toggle');
    document.getElementById("uploadingProgressMessage").style.display = "block";
    document.getElementById("close-intro-button").style.display = "none";
  }
  // function getCheckedAttributes(){
  //   $("input:checkbox[name=type]:checked").each(function(){
  //       console.log($(this).val());
  // }

  $("#define-attributes").on('submit', function(e) {

    var attributeList =  [];
    var selectedAxis =  [];
    // var xList = [];
    // var yList = [];
    // var zList = [];

    $(".types").each(function(){ 
        var selectedXOption = $(this).children("option").filter(":selected").text()
        attributeList.push($(this).val() + " " + selectedXOption);
        selectedAxis.push(["true", "true", "true"])
        // var selectedYOption = $(this).children("option").filter(":selected").text()
        // yList.push($(this).val() + " " + selectedYOption);
        // var selectedZOption = $(this).children("option").filter(":selected").text()
        // zList.push($(this).val() + " " + selectedZOption);
    });

    var selectedAttributes = filterUncheckAttributes(attributeList,selectedAxis);
    var selectedAttributesParsed = [];
    for (i = 0; i < selectedAttributes.length; i++) {
        temp = selectedAttributes[i].split(" ");
        console.log("final selected: ", temp);
        selectedAttributesParsed.push({name:temp[0],type:temp[1],selectedX:temp[2],selectedY:temp[3],selectedZ:temp[4]})

    }

    window.selectedAttributesParsed = selectedAttributesParsed;
    //$('#define-attributes').modal('toggle');

    //XuMo: Add a modal for join key selection 

    /** #for split table
    if(document.getElementById("split-table-or-not").checked == true){
      //XuMo: Add zattribute variable to store z values here
      var zAttributesForJoinKey = [];
      for (i = 0; i < selectedAttributesParsed.length; i++) {
          if (selectedAttributesParsed[i]["selectedZ"] == "true") {
            zAttributesForJoinKey.push(selectedAttributesParsed[i]["name"]);
          }
      }
      var zAttrubutesName = "";
      var joinKeySelect = "";
      for (i = 0; i < zAttributesForJoinKey.length; i++) {
        zAttrubutesName += "<tr> <td><div style='margin-bottom: 1px;'>"  + zAttributesForJoinKey[i] +
        "</div></td> </tr>";
        joinKeySelect += "<tr> <td>" + "<input type='radio' value = '" + zAttributesForJoinKey[i] + "' id ='join-key-name' style = 'margin-left: 12px; margin-right: 12px;margin-bottom: 4px;'>" + "</td></tr>";
      }
      $('.join-key-candidates').html(zAttrubutesName);
      $('.join-key-select-or-not').html(joinKeySelect);
      $('#join-key-selection').modal('toggle');
      return;
    }
    **/

    datatable_creation();

  });

  $("#join-key-selection").on('submit', function(e) {
    var object_for_join_key = {
      "username": $cookies.getObject('userinfo')['username'][0],
      "dataset": datasetNameInput,
      "join_key": document.getElementById("join-key-name").value.toString(), 
    };

    $.ajax({
                type: "POST",
                url: "/zv/join_key_adder", 
                data: object_for_join_key,
                async: false,
                success: function(response){
                  
                },
                error: function() {
                  alert("failed to add join key to database");
                } 
    });


    datatable_creation();   
  });

  function datatable_creation() {
    var xyzQuery = {datasetName:datasetNameInput, variables:selectedAttributesParsed};
    console.log(xyzQuery);
    var myObject = JSON.stringify(xyzQuery);

    var username;
    if($cookies.getObject('userinfo')){
      username = $cookies.getObject('userinfo')['username'][0];
    }else{
      username = "public";
    }
    var insertUserTablePair = {'userName':username, 'datasetName':formData.get("datasetName")};
    if(formData.get("dataInputTextbox") != ""){
        formData.set("datasetName","!!" + formData.get("datasetName") + " "+ formData.get("dataInputTextbox"))
    }
    $.ajax({ 
        type: 'POST',
        url: '/zv/selectXYZ', 
        data: myObject,
        contentType: 'application/json; charset=utf-8',
        success: function (data) { console.log('called1');
        $.ajax({
            url : '/zv/fileUpload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {console.log('called2');
                $.ajax({
                  url : '/zv/insertUserTablePair',
                  type: 'POST',
                  data: insertUserTablePair,
                  success: function(response){

                    // if(username == 'public'){
                    //   alert("Inserted as public dataset");
                    // }else{
                      var today = new Date();
                      var expiresValue = new Date(today);
                      //Set 'expires' option in 2 hours
                      expiresValue.setMinutes(today.getMinutes() + 120);
                      $cookies.putObject("userinfo",response,{'expires': expiresValue})
                      // angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
                      datasetInfo.storetablelist(response['tablelist']);
                      $scope.tablelist = datasetInfo.getTablelist();
                      // console.log("table inserted into your account successfully")
                    // }

                  },
                  error: function(response){
                    alert("fail to insert dataset into your account")
                  }
                });

                $('#dataset-form-control').append($("<option></option>")
                              .attr("value", formData.get("datasetName"))
                              .text( formData.get("datasetName")));
                              document.getElementById("uploadingProgressMessage").style.display = "block";
                              document.getElementById("dataset-upload-submit").style.display = "none";
                alert("Upload successful");
                location.reload();
            },
            error: function (jXHR, textStatus, errorThrown) {
              $("#errorModalText").html("Server error. Have you tried the dataset upload instructions in the following link? <a href='https://github.com/zenvisage/zenvisage/wiki/Instructions-for-uploading-new-datasets'>here?</a>");
              $("#errorModal").modal()
                document.getElementById("uploadingProgressMessage").style.display = "block";
                document.getElementById("dataset-upload-submit").style.display = "none";
            }
        });

        },
        error: function (jXHR, textStatus, errorThrown) {
          $("#errorModalText").html("Server error. Have you tried the dataset upload instructions in the following link? <a href='https://github.com/zenvisage/zenvisage/wiki/Instructions-for-uploading-new-datasets'>here?</a>");
          $("#errorModal").modal()
          document.getElementById("uploadingProgressMessage").style.display = "block";
          document.getElementById("dataset-upload-submit").style.display = "none";

        }
    });
  }

}]);
