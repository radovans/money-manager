# money-manager

Multipurpose application for managing my money.
- process monthly bank statements and apply rules to categorize transactions
- track past transactions and create spending reports
- plan future transactions and create budget reports
- track assets and liabilities

Run docker container with postgres database:
docker run -d -p 5432:5432 --name moneymng --restart always -e POSTGRES_USER=moneymng -e POSTGRES_PASSWORD=moneymng -e POSTGRES_DB=moneymng -v moneymng_postgres_data:/var/lib/postgresql/data postgres

TODO UI backlog:
DONE - add boxes around all componenents, not only in dashboard
DONE - move filters to flex with header
- add aggregate sum to transaction list in datagrid transactions.jsx
- create button group https://mui.com/material-ui/react-button-group/ for Clear, All, This year, Last year
DONE - Categories pie chart
DONE  - add total sum of visualized data from pie chart next to the header
DONE  - next to the pie chart add table with 10 most expensive transactions from actual view (from all transactions/from selected main category)
DONE  - create new pie chart for spending by category (input is main category)
DONE  - add on click action from main category pie chart to category pie chart, so you can see detail of the main category 
DONE - add loading indicator to all charts if data are not loaded yet
DONE - add fixed height of the bars in bar charts
- create upload button in import page which will accept csv file
- create input form in import page for manual input of transactions
- create line chart in /incomes for salary only with switch
- create bar chart for spending by expense nature (must, need, want)
- create table for cash flow
- create table for assets and liabilities
- create line chart for predicting future balance, income and expenses

TODO Backend backlog:
- add expense type by nature (must, need, want)
- add transaction type (Direct debit, Standing order, Regular payment, Subscription, One-time payment)
- create docker-compose with database, backend and frontend
DONE - search should ignore input case

Líniový graf - Kumulovaný vývoj zostatku - vs minulé obdobie v percentách
Prijem a vydaje opacnymi smermi  v jednom grafe. Prijmy hore vydaje dole
Kalendar s dennym spendingom/income a kumulativnym
Porovnanie jednotlivych period
Last year, this year, difference, in percent