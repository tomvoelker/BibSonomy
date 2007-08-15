<%-- admin navigation bar --%>
<p style="font-weight: bold; 
          background: #eee; 
          border-bottom: 1px solid #ccc;
          border-top: 1px solid #ccc;
          margin: 0px;
          padding: 0px 10px 0px 10px;">
          
<a href="/admin_statistics.jsp?showSpammer=no">statistics</a> &middot;

<a href="/admin_spammertags.jsp">spammertags</a> &middot;

<!-- <a href="/admin_dblp.jsp">DBLP</a> &middot; -->

<a href="/admin.jsp">admin</a> &middot;

<a style="border: 1px solid #aaa; padding:2px; background: #eee;" title="killSpammer" href="javascript:location.href='${projectHome}admin.jsp?action=flag_spammer&amp;user='+encodeURIComponent(location.href.replace(/.*\//, ''))" inclick="return false;">SpammerKillerButton</a>

</p>