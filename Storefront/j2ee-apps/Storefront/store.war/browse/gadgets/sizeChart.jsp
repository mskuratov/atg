<%--
  This gadget renders a size chart popup.
  This page displays size chart for:
    Women's Shirt;
    Men's Shirt;
    Women's Shoes;
    Men's Shoes.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <crs:popupPageContainer divId="atg_store_sizeChart"
                          titleKey="browse_sizeChart.title">
    <%-- Women's Shirt sizes --%>
    <fmt:message var="womenShirtSizesTableSummary" key="browse_sizeChart.womenShirtSizesTableSummary"/>
    <table id="atg_store_sizeChartTable" summary="${womenShirtSizesTableSummary}">
      <tbody>
        <tr class="atg_store_sizeChartType">
          <td colspan="8"><fmt:message key="browse_sizeChart.womenShirt" /></td>
        </tr>

        <tr class="atg_store_sizeChartSizes">
          <th id="ws_col1" scope="col"><fmt:message key="browse_sizeChart.usSize" /></th>
          <th id="ws_col2" scope="col"><fmt:message key="browse_sizeChart.usSize" /></th>          
          <th id="ws_col3" scope="col"><fmt:message key="browse_sizeChart.euSize" /></th>          
          <th id="ws_col4" scope="col"><fmt:message key="browse_sizeChart.bust" /></th>
          <th id="ws_col5" scope="col"><fmt:message key="browse_sizeChart.sleeve" /></th>
          <th id="ws_col6" scope="col"><fmt:message key="browse_sizeChart.waist" /></th>
          <th id="ws_col7" scope="col"><fmt:message key="browse_sizeChart.hip" /></th>
          <th id="ws_col8" scope="col"><fmt:message key="browse_sizeChart.inseam" /></th>
        </tr>
        <tr>
          <td headers="ws_col1">0</td>
          <td headers="ws_col2">S</td>
          <td headers="ws_col3">30</td>
          <td headers="ws_col4">31"</td>
          <td headers="ws_col5">29 3/8"</td>
          <td headers="ws_col6">24"</td>
          <td headers="ws_col7">33"</td>
          <td headers="ws_col8">33"</td>
        </tr>
        <tr>
          <td>2</td>
          <td>S</td>
          <td>32</td>
          <td>32"</td>
          <td>29 3/8"</td>
          <td>25"</td>
          <td>34"</td>
          <td>33"</td>
        </tr>
        <tr>
          <td>4</td>
          <td>M</td>
          <td>34</td>
          <td>33"</td>
          <td>30"</td>
          <td>26"</td>
          <td>35"</td>
          <td>33"</td>
        </tr>
        <tr>
          <td>6</td>
          <td>M</td>
          <td>36</td>
          <td>34"</td>
          <td>31"</td>
          <td>27"</td>
          <td>36"</td>
          <td>33"</td>
        </tr>
        <tr>
          <td>8</td>
          <td>L</td>
          <td>38</td>
          <td>35"</td>
          <td>31 1/2"</td>
          <td>28"</td>
          <td>37"</td>
          <td>33"</td>
        </tr>
        <tr>
          <td>10</td>
          <td>L</td>
          <td>40</td>
          <td>36"</td>
          <td>32 1/8"</td>
          <td>29"</td>
          <td>38"</td>
          <td>33"</td>
        </tr>
        <tr>
          <td>12</td>
          <td>XL</td>
          <td>42</td>
          <td>37"</td>
          <td>32 3/8"</td>
          <td>30 1/2"</td>
          <td>39"</td>
          <td>33"</td>
        </tr>
        <tr>
          <td>14</td>
          <td>XXL</td>
          <td>44</td>
          <td>38"</td>
          <td>32 5/8"</td>
          <td>32"</td>
          <td>40 1/2"</td>
          <td>33"</td>
        </tr>
      </tbody>
    </table>

    <%-- Men's Shirt sizes --%>
    <fmt:message var="menShirtSizesTableSummary" key="browse_sizeChart.menShirtSizesTableSummary"/>
    <table id="atg_store_sizeChartTable" summary="${menShirtSizesTableSummary}">
      <tbody>
        <tr class="atg_store_sizeChartType">
          <td colspan="6"><fmt:message key="browse_sizeChart.menShirt" /></td>
        </tr>
        <tr class="atg_store_sizeChartSizes">
          <th id="ms_col1" scope="col"><fmt:message key="browse_sizeChart.usSize" /></th>
          <th id="ms_col2" scope="col"><fmt:message key="browse_sizeChart.euSize" /></th>
          <th id="ms_col3" scope="col"> <fmt:message key="browse_sizeChart.neck" /></th>
          <th id="ms_col4" scope="col"><fmt:message key="browse_sizeChart.chest" /></th>
          <th id="ms_col5" scope="col"><fmt:message key="browse_sizeChart.waist" /></th>
          <th id="ms_col6" scope="col"><fmt:message key="browse_sizeChart.sleeve" /></th>
        </tr>
        <tr>
          <td headers="ms_col1">S</td>
          <td headers="ms_col2">87</td>
          <td headers="ms_col3">14-14"</td>
          <td headers="ms_col4">34-36"</td>
          <td headers="ms_col5">28-30"</td>
          <td headers="ms_col6">32-33"</td>
        </tr>
        <tr>
          <td>M</td>
          <td>91</td>
          <td>15-15"</td>
          <td>38-40"</td>
          <td>32-34"</td>
          <td>33-34"</td>
        </tr>
        <tr>
          <td>L</td>
          <td>102</td>
          <td>16-16"</td>
          <td>42-44"</td>
          <td>36-38"</td>
          <td>34-35"</td>
        </tr>
        <tr>
          <td>XL</td>
          <td>107</td>
          <td>17-17"</td>
          <td>42-44"</td>
          <td>40-42"</td>
          <td>35-35"</td>
        </tr>
        <tr>
          <td>XXL</td>
          <td>117</td>
          <td>18-18"</td>
          <td>50-52"</td>
          <td>44-46"</td>
          <td>35-36"</td>
        </tr>
      </tbody>
    </table>

    <%-- Women's Shoes sizes --%>    
    <fmt:message var="womenShoesSizesTableSummary" key="browse_sizeChart.womenShoesSizesTableSummary"/>
    <table id="atg_store_sizeChartTable" summary="${womenShoesSizesTableSummary}">
      <tbody>
        <tr class="atg_store_sizeChartType">
          <td colspan="5"><fmt:message key="browse_sizeChart.womenShoes" /></td>
        </tr>
        <tr class="atg_store_sizeChartSizes">
          <th id="wsh_col1" scope="col"><fmt:message key="browse_sizeChart.us" /></th>
          <th id="wsh_col2" scope="col"><fmt:message key="browse_sizeChart.uk" /></th>
          <th id="wsh_col3" scope="col"><fmt:message key="browse_sizeChart.eu" /></th>
          <th id="wsh_col4" scope="col"><fmt:message key="browse_sizeChart.jp" /></th>
          <th id="wsh_col5" scope="col"><fmt:message key="browse_sizeChart.mx" /></th>
        </tr>
        <tr>
          <td headers="wsh_col1">5</td>
          <td headers="wsh_col2">3 1/2</td>
          <td headers="wsh_col3">35 1/2</td>
          <td headers="wsh_col4">22</td>
          <td headers="wsh_col5">3</td>
        </tr>
        <tr>
          <td>5 1/2</td>
          <td>4</td>
          <td>36</td>
          <td>22 1/2</td>
          <td>3 1/2</td>
        </tr>
        <tr>
          <td>6</td>
          <td>4 1/2</td>
          <td>36 1/2</td>
          <td>23</td>
          <td>4</td>
        </tr>
        <tr>
          <td>6 1/2</td>
          <td>5</td>
          <td>37</td>
          <td>23 1/2</td>
          <td>4 1/2</td>
        </tr>
        <tr>
          <td>7</td>
          <td>5 1/2</td>
          <td>37 1/2</td>
          <td>24</td>
          <td>5</td>
        </tr>
        <tr>
          <td>7 1/2</td>
          <td>6</td>
          <td>38</td>
          <td>24 1/2</td>
          <td>5 1/2</td>
        </tr>
        <tr>
          <td>8</td>
          <td>6 1/2</td>
          <td>38 1/2</td>
          <td>25</td>
          <td>6</td>
        </tr>
        <tr>
          <td>8 1/2</td>
          <td>7</td>
          <td>39</td>
          <td>25 1/2</td>
          <td>6 1/2</td>
        </tr>
        <tr>
          <td>9</td>
          <td>7 1/2</td>
          <td>39 1/2</td>
          <td>26</td>
          <td>7</td>
        </tr>
        <tr>
          <td>9 1/2</td>
          <td>8</td>
          <td>40</td>
          <td>26 1/2</td>
          <td>7 1/2</td>
        </tr>
        <tr>
          <td>10</td>
          <td>8 1/2</td>
          <td>40 1/2</td>
          <td>27</td>
          <td>8</td>
        </tr>
      </tbody>
    </table>

    <%-- Men's Shoes sizes --%>
    <fmt:message var="menShoesSizesTableSummary" key="browse_sizeChart.menShoesSizesTableSummary"/>
    <table id="atg_store_sizeChartTable" summary="${menShoesSizesTableSummary}">
      <tbody>
        <tr class="atg_store_sizeChartType">
          <td colspan="5"><fmt:message key="browse_sizeChart.menShoes" /></td>
        </tr>
        <tr class="atg_store_sizeChartSizes">
          <th id="msh_col1" scope="col"><fmt:message key="browse_sizeChart.us" /></th>
          <th id="msh_col2" scope="col"><fmt:message key="browse_sizeChart.uk" /></th>
          <th id="msh_col3" scope="col"><fmt:message key="browse_sizeChart.eu" /></th>
          <th id="msh_col4" scope="col"><fmt:message key="browse_sizeChart.jp" /></th>
          <th id="msh_col5" scope="col"><fmt:message key="browse_sizeChart.mx" /></th>
        </tr>
        <tr>
          <td headers="msh_col1">8</td>
          <td headers="msh_col2">6 1/2</td>
          <td headers="msh_col3">41 1/2</td>
          <td headers="msh_col4">25</td>
          <td headers="msh_col5">7</td>
        </tr>
        <tr>
          <td>8 1/2</td>
          <td>7</td>
          <td>42</td>
          <td>25 1/2</td>
          <td>7 1/2</td>
        </tr>
        <tr>
          <td>9</td>
          <td>7 1/2</td>
          <td>42 1/2</td>
          <td>26</td>
          <td>8</td>
        </tr>
        <tr>
          <td>9 1/2</td>
          <td>8</td>
          <td>43</td>
          <td>26 1/2</td>
          <td>8 1/2</td>
        </tr>
        <tr>
          <td>10</td>
          <td>8 1/2</td>
          <td>43 1/2</td>
          <td>27</td>
          <td>9</td>
        </tr>
        <tr>
          <td>10 1/2</td>
          <td>9</td>
          <td>44</td>
          <td>27 1/2</td>
          <td>9 1/2</td>
        </tr>
        <tr>
          <td>11</td>
          <td>9 1/2</td>
          <td>44 1/2</td>
          <td>28</td>
          <td>10</td>
        </tr>
        <tr>
          <td>11 1/2</td>
          <td>10</td>
          <td>45</td>
          <td>28 1/2</td>
          <td>10 1/2</td>
        </tr>
        <tr>
          <td>12</td>
          <td>10 1/2</td>
          <td>45 1/2</td>
          <td>29</td>
          <td>11</td>
        </tr>
        <tr>
          <td>12 1/2</td>
          <td>11</td>
          <td>46</td>
          <td>29 1/2</td>
          <td>11 1/2</td>
        </tr>
        <tr>
          <td>13</td>
          <td>11 1/2</td>
          <td>46 1/2</td>
          <td>30</td>
          <td>12</td>
        </tr>
      </tbody>
    </table>
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/sizeChart.jsp#1 $$Change: 735822 $--%>

