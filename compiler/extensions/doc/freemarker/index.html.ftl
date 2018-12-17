<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script src="jquery.js"></script>
  </head>
  <body>
    <table>
      <tr>
        <td>
          <div id='packages' style="overflow:scroll; height:500px"></div>
          <div id='overview' style="overflow:scroll; height:500px"></div>
        </td>
        <td valign=top>
          <div id='content'></div>
        </td>
      </tr>
    </table>

    <script>
      function loadContent(fileName)
      {
        $.get(fileName, function(data) {
          $("#content").html(data);
        }, "text");
      }

      $(document).ready(function(){
        $.get('packages.html', function(data) {
            $("#packages").html(data);
        }, "text");

        $.get('overview.html', function(data) {
            $("#overview").html(data);
        }, "text");
      });
    </script>
  </body>
</html>
