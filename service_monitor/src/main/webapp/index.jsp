
<%
	Cookie[] cookies = request.getCookies();
	if (cookies != null)
	{
		boolean hasApiClientId = false;
		for (Cookie cookie : cookies)
		{
			if(cookie.getName().equals("apiClientId"))
			{
				hasApiClientId = true;
			}
		}
		if(!hasApiClientId)
		{
			Cookie cookie = new Cookie("apiClientId","0d466d06-2b57-4669-b59d-56209c58d361");
			cookie.setMaxAge(365 * 24 * 60 * 60);
			response.addCookie(cookie);
		}
	}
%>

<html>
<head>
<script src="resources/third-party/js/jquery.js"></script>
<script src="resources/third-party/js/bootstrap.js"></script>
<script src="resources/third-party/js/r.js"></script>
<script src="resources/inhouse/js/autostart.js"></script>

<link href="resources/third-party/css/bootstrap.min.css"
	rel="stylesheet">
<link href="resources/third-party/css/metisMenu.min.css"
	rel="stylesheet">
<link href="resources/third-party/css/responsivedesign.css"
	rel="stylesheet">
<link href="resources/third-party/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">
<link href="resources/inhouse/css/loading.css" rel="stylesheet">
<link href="resources/inhouse/css/graph.css" rel="stylesheet">
<link href="resources/inhouse/css/navbar.css" rel="stylesheet">

</head>
<body>
	<div class="custom-overlay">
		<div id="loading-img"></div>
	</div>
	<!-- Modal -->
	<div class="modal fade" id="detailedView" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="detailedViewTitle"></h4>
				</div>
				<div class="modal-body" id="detailedViewBody"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div id="wrapper">
		<div id="nav-wrapper"></div>
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Dashboard</h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<div id="cardswrapper"></div>
		</div>
	</div>
</body>
</html>
