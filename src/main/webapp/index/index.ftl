<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <title>城市设置</title>
    <script src="/static/js/jquery-2.2.1.js"></script>
<#--<link href="../../resources/css/public.css" rel="stylesheet">-->
    <script type="text/javascript">

    </script>
    <style type="text/css">

    </style>
</head>
<body>
<div class="mainContainer">
    <div class="changeContainer">
        <div class="search">
            <form id="searchForm" role="form" onsubmit="return false;">
                <input type="hidden" name="type" value="1" id="search_type">
                <ul>
                    <li><span>城市名称：</span><input type="text" name="name" id="search_name"></li>
                    <li class="fr">
                        <button id="multiDelBtn" type="submit" class="btn red">批量删除</button>
                    </li>
                    <li class="fr">
                        <button id="addDialogBtn" type="submit" class="btn red openPopUpBoxBtn">增加城市</button>
                    </li>
                    <li class="fr">
                        <button id="searchBtn" type="submit" class="btn red">查询</button>
                    </li>

                </ul>
            </form>
        </div>
        <div class="table_style">
            <table class="table">
                <thead>
                <tr>
                    <th width="5%">选择</th>
                    <th width="12%">城市简称</th>
                    <th width="10%">城市名称</th>
                    <th width="20%">操作</th>
                </tr>
                </thead>

                <tbody id="table-tbody">

                </tbody>
            </table>
        </div>
        <div class="pagination">

        </div>
    </div>
</div>

</body>
</html>