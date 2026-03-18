Currency sync source:
Configured by `pakgopay.currency-sync.excel-url` in application.yml

Expected first sheet column order:
1. currencyType
2. name
3. icon
4. currencyAccuracy
5. timezone

Existing currencies in database will be skipped and never updated by sync.
