# money-manager

Multipurpose application for managing my money.
- process monthly bank statements and apply rules to categorize transactions
- track past transactions and create spending reports
- plan future transactions and create budget reports
- track assets and liabilities

Run docker container with postgres database:
docker run -d -p 5432:5432 --name moneymng --restart always -e POSTGRES_USER=moneymng -e POSTGRES_PASSWORD=moneymng -e POSTGRES_DB=moneymng -v moneymng_postgres_data:/var/lib/postgresql/data postgres

TODO backlog:
DASHBOARD:
DONE - create dashboard site

TRANSACTIONS:
DONE - create transactions site
DONE - move filters to flex with header
DONE - add aggregate sum to transaction list in datagrid transactions.jsx
DONE - create button group https://mui.com/material-ui/react-button-group/ for Clear, All, This year, Last year
DONE - create input form for creating new transactions

RULES:
DONE - create rules site
DONE - input form in rules page for manual input of rules
DONE - download button in rules page which will download rules as json
DONE - add possiblity to skip transaction in rules

IMPORT:
DONE - create import site
- upload button in import page which will accept csv file with transactions
- response from parsed transactions should return list of successfully parsed transactions and list of errors
- upload button in import page which will accept json configuration
DONE - download button in import page which will download json configuration

BALANCE:
DONE - create balance site

INCOMES:
DONE - create incomes site
DONE - create line chart in /incomes for salary only with switch

EXPENSES:
DONE - create expenses site
- create bar chart for spending by expense nature (must, need, want)

CATEGORIES:
DONE - create categories site
DONE - categories pie chart
DONE  - add total sum of visualized data from pie chart next to the header
DONE  - next to the pie chart add table with 10 most expensive transactions from actual view (from all transactions/from selected main subcategory)
DONE  - create new pie chart for spending by subcategory (input is main subcategory)
DONE  - add on click action from main subcategory pie chart to subcategory pie chart, so you can see detail of the main subcategory
DONE - add fixed height of the bars in bar charts

CUMULATIVE BALANCE:
DONE - create cumulative balance site

COMMON:
DONE - add boxes around all componenents, not only in dashboard
DONE - add loading indicator to all charts if data are not loaded yet
DONE - create dropdown menu for accounts, categories, rules, featured transactions, recurrent transactions, import/export
- import transactions should use stepper https://mui.com/material-ui/react-stepper/ , first step is upload csv file, second step is validate data, show errors and update data, third step is save data
- create table for cash flow
- create table for assets and liabilities
- create line chart for predicting future balance, income and expenses
- add icons to categories
- add expense type by nature (must, need, want)
- add transaction type (Direct debit, Standing order, Regular payment, Subscription, One-time payment)
- create docker-compose with database, backend and frontend
DONE - search should ignore input case

FIX - http://localhost:8088/categories?category=null

Líniový graf - Kumulovaný vývoj zostatku - vs minulé obdobie v percentách
Prijem a vydaje opacnymi smermi  v jednom grafe. Prijmy hore vydaje dole
Kalendar s dennym spendingom/income a kumulativnym
Porovnanie jednotlivych period
Last year, this year, difference, in percent