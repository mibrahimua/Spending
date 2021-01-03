DROP TABLE IF EXISTS transaction_spend;CREATE TABLE IF NOT EXISTS transaction_spend (`transactionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transactionType` TEXT NOT NULL, `transactionIncome` REAL, `transactionExpense` REAL, `categoryId` INTEGER NOT NULL, `transactionDate` INTEGER NOT NULL, `transactionNote` TEXT);DROP TABLE IF EXISTS currency;CREATE TABLE IF NOT EXISTS currency (`currencyId` TEXT NOT NULL, `currencyName` TEXT, `currencySymbol` TEXT, PRIMARY KEY(`currencyId`));DROP TABLE IF EXISTS category;CREATE TABLE IF NOT EXISTS category (`categoryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryName` TEXT, `iconId` INTEGER NOT NULL, `categoryType` TEXT NOT NULL);DROP TABLE IF EXISTS icon_category;CREATE TABLE IF NOT EXISTS icon_category (`iconId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `iconName` TEXT NOT NULL);DROP TABLE IF EXISTS account;CREATE TABLE IF NOT EXISTS account (`userId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userName` TEXT NOT NULL, `userEmail` TEXT NOT NULL);CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a2fa1630227fb6066b36ea0445708b19');INSERT INTO icon_category (iconId, iconName) VALUES (1, 'ic_food_candy_2');INSERT INTO icon_category (iconId, iconName) VALUES (2, 'ic_food_candy_8');INSERT INTO icon_category (iconId, iconName) VALUES (3, 'ic_food_candy_12');INSERT INTO icon_category (iconId, iconName) VALUES (4, 'ic_food_candy_14');INSERT INTO icon_category (iconId, iconName) VALUES (5, 'ic_food_candy_18');INSERT INTO icon_category (iconId, iconName) VALUES (6, 'ic_food_candy_22');INSERT INTO icon_category (iconId, iconName) VALUES (7, 'ic_food_candy_24');INSERT INTO icon_category (iconId, iconName) VALUES (8, 'ic_food_candy_30');INSERT INTO icon_category (iconId, iconName) VALUES (9, 'ic_food_eat_6_thin');INSERT INTO icon_category (iconId, iconName) VALUES (10, 'ic_food_fast_food_2');INSERT INTO icon_category (iconId, iconName) VALUES (11, 'ic_food_fast_food_6');INSERT INTO icon_category (iconId, iconName) VALUES (12, 'ic_food_fast_food_8');INSERT INTO icon_category (iconId, iconName) VALUES (13, 'ic_food_fast_food_10');INSERT INTO icon_category (iconId, iconName) VALUES (14, 'ic_food_fast_food_14');INSERT INTO icon_category (iconId, iconName) VALUES (15, 'ic_food_fast_food_16');INSERT INTO icon_category (iconId, iconName) VALUES (16, 'ic_food_fast_food_18');INSERT INTO icon_category (iconId, iconName) VALUES (17, 'ic_food_fast_food_20');INSERT INTO icon_category (iconId, iconName) VALUES (18, 'ic_shopping_glasses_7');INSERT INTO icon_category (iconId, iconName) VALUES (19, 'ic_shopping_shopping_bag_2');INSERT INTO icon_category (iconId, iconName) VALUES (20, 'ic_shopping_shopping_bag_6');INSERT INTO icon_category (iconId, iconName) VALUES (21, 'ic_shopping_shopping_bag_5');INSERT INTO icon_category (iconId, iconName) VALUES (22, 'ic_shopping_shopping_cart_24');INSERT INTO icon_category (iconId, iconName) VALUES (23, 'ic_shopping_shopping_cart_2');INSERT INTO icon_category (iconId, iconName) VALUES (24, 'ic_transportation_airport_15');INSERT INTO icon_category (iconId, iconName) VALUES (25, 'ic_transportation_airport_17');INSERT INTO icon_category (iconId, iconName) VALUES (26, 'ic_transportation_airport_19');INSERT INTO icon_category (iconId, iconName) VALUES (27, 'ic_transportation_basket_4');INSERT INTO icon_category (iconId, iconName) VALUES (28, 'ic_transportation_bus_8');INSERT INTO icon_category (iconId, iconName) VALUES (29, 'ic_transportation_car_2');INSERT INTO icon_category (iconId, iconName) VALUES (30, 'ic_transportation_car_6');INSERT INTO icon_category (iconId, iconName) VALUES (31, 'ic_transportation_car_20');INSERT INTO icon_category (iconId, iconName) VALUES (32, 'ic_transportation_car_23');INSERT INTO icon_category (iconId, iconName) VALUES (33, 'ic_shopping_shopping_cart_2');INSERT INTO icon_category (iconId, iconName) VALUES (34, 'ic_shopping_shopping_cart_2');INSERT INTO icon_category (iconId, iconName) VALUES (35, 'ic_shopping_shopping_cart_2');INSERT INTO icon_category (iconId, iconName) VALUES (36, 'ic_entertainments_paintbrush_7');INSERT INTO icon_category (iconId, iconName) VALUES (37, 'ic_entertainments_video_12');INSERT INTO icon_category (iconId, iconName) VALUES (38, 'ic_entertainments_video_3');INSERT INTO icon_category (iconId, iconName) VALUES (39, 'ic_entertainments_video_9');INSERT INTO icon_category (iconId, iconName) VALUES (40, 'ic_entertainments_video_camera_12');INSERT INTO icon_category (iconId, iconName) VALUES (41, 'ic_entertainments_video_camera_thin');INSERT INTO icon_category (iconId, iconName) VALUES (42, 'ic_games_gamepad_4');INSERT INTO icon_category (iconId, iconName) VALUES (43, 'ic_games_puzzle_2');INSERT INTO icon_category (iconId, iconName) VALUES (44, 'ic_games_steam_5');INSERT INTO icon_category (iconId, iconName) VALUES (45, 'ic_health_medical_11');INSERT INTO icon_category (iconId, iconName) VALUES (46, 'ic_health_medical_13');INSERT INTO icon_category (iconId, iconName) VALUES (47, 'ic_health_medical_15');INSERT INTO icon_category (iconId, iconName) VALUES (48, 'ic_health_medical_16');INSERT INTO icon_category (iconId, iconName) VALUES (49, 'ic_health_medical_17');INSERT INTO icon_category (iconId, iconName) VALUES (50, 'ic_health_medical_4');INSERT INTO icon_category (iconId, iconName) VALUES (51, 'ic_health_medical_5');INSERT INTO icon_category (iconId, iconName) VALUES (52, 'ic_health_medical_7');INSERT INTO icon_category (iconId, iconName) VALUES (53, 'ic_money_banknote_12');INSERT INTO icon_category (iconId, iconName) VALUES (54, 'ic_money_banknote_15');INSERT INTO icon_category (iconId, iconName) VALUES (55, 'ic_money_christmas_42');INSERT INTO icon_category (iconId, iconName) VALUES (56, 'ic_money_credit_card_16');INSERT INTO icon_category (iconId, iconName) VALUES (57, 'ic_sports_baseball_2');INSERT INTO icon_category (iconId, iconName) VALUES (58, 'ic_sports_basketball_2');INSERT INTO icon_category (iconId, iconName) VALUES (59, 'ic_sports_building_40');INSERT INTO icon_category (iconId, iconName) VALUES (60, 'ic_sports_golf_2');INSERT INTO icon_category (iconId, iconName) VALUES (61, 'ic_sports_soccer_1');INSERT INTO icon_category (iconId, iconName) VALUES (62, 'ic_transportation_bicycle_1');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (1, 'Food', 9, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (2, 'Transportation', 28, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (3, 'Shopping', 23, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (4, 'Entertainment', 36, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (5, 'Fitness', 59, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (6, 'Medical', 52, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (7, 'Family', 1, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (8, 'Furniture', 1, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (9, 'Electronics', 1, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (10, 'Education', 1, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (11, 'Personal', 1, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (12, 'Life', 1, 'EXPENSE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (13, 'Income', 53, 'INCOME');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (99, 'Previous Balance', 54, 'BALANCE');INSERT INTO category (categoryId, categoryName, iconId, categoryType) VALUES (100, 'Custom Category', 1, 'CUSTOM');INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 2, 0, 50000.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('INCOME', 6, 60000.0, 0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 6, 0, 51200.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 5, 0, 533200.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 3, 0, 50660.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 11, 0, 78980.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 3, 0, 500230.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 1, 0, 3120.0, 1603126800000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 6, 0, 60280.0, 1607423070000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('INCOME', 13, 10000.0, 0, 1607423070000);INSERT INTO transaction_spend (transactiontype, categoryId, transactionIncome, transactionExpense, transactionDate) VALUES ('EXPENSE', 3, 0, 17000.0, 1607423070000);INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('AFN', 'Afghan Afghani', '؋');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ALL', 'Albanian Lek', 'L');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('DZD', 'Algerian Dinar', 'دج');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('AOA', 'Angolan Kwanza', 'Kz');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ARS', 'Argentine Peso', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('AMD', 'Armenian Dram', '֏');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('AWG', 'Aruban Florin', 'ƒ');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('AUD', 'Australian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('AZN', 'Azerbaijani Manat', '₼');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BSD', 'Bahamian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BHD', 'Bahraini Dinar', '.د.ب');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BDT', 'Bangladeshi Taka', '৳');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BBD', 'Barbadian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BYR', 'Belarusian Ruble', 'Br');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BZD', 'Belize Dollar', 'BZ$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BMD', 'Bermudan Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BTN', 'Bhutanese Ngultrum', 'Nu.');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BTC', 'Bitcoin', '฿');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BOB', 'Bolivian Boliviano', '$b');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BAM', 'Bosnia-Herzegovina Convertible Mark', 'KM');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BWP', 'Botswanan Pula', 'P');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BRL', 'Brazilian Real', 'R$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GBP', 'British Pound Sterling', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BND', 'Brunei Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BGN', 'Bulgarian Lev', 'лв');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('BIF', 'Burundian Franc', 'FBu');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KHR', 'Cambodian Riel', '៛');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CAD', 'Canadian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CVE', 'Cape Verdean Escudo', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KYD', 'Cayman Islands Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('XOF', 'CFA Franc BCEAO', 'CFA');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('XAF', 'CFA Franc BEAC', 'FCFA');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('XPF', 'CFP Franc', '₣');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CLP', 'Chilean Peso', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CNY', 'Chinese Yuan', '¥');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('COP', 'Colombian Peso', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KMF', 'Comorian Franc', 'CF');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CDF', 'Congolese Franc', 'FC');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CRC', 'Costa Rican Colón', '₡');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('HRK', 'Croatian Kuna', 'kn');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CUP', 'Cuban Peso', '₱');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CZK', 'Czech Republic Koruna', 'Kč');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('DKK', 'Danish Krone', 'kr');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('DJF', 'Djiboutian Franc', 'Fdj');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('DOP', 'Dominican Peso', 'RD$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('XCD', 'East Caribbean Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('EGP', 'Egyptian Pound', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ERN', 'Eritrean Nakfa', 'Nfk');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ETB', 'Ethiopian Birr', 'Br');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('EUR', 'Euro', '€');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('FKP', 'Falkland Islands Pound', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('FJD', 'Fijian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GMD', 'Gambian Dalasi', 'D');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GEL', 'Georgian Lari', '₾');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GHS', 'Ghanaian Cedi', 'GH₵');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GIP', 'Gibraltar Pound', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GTQ', 'Guatemalan Quetzal', 'Q');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GNF', 'Guinean Franc', 'FG');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('GYD', 'Guyanaese Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('HTG', 'Haitian Gourde', 'G');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('HNL', 'Honduran Lempira', 'L');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('HKD', 'Hong Kong Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('HUF', 'Hungarian Forint', 'Ft');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ISK', 'Icelandic Króna', 'kr');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('INR', 'Indian Rupee', '₹');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('IDR', 'Indonesian Rupiah', 'Rp');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('IRR', 'Iranian Rial', '﷼');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('IQD', 'Iraqi Dinar', 'ع.د');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ILS', 'Israeli New Sheqel', '₪');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('JMD', 'Jamaican Dollar', 'J$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('JPY', 'Japanese Yen', '¥');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('JOD', 'Jordanian Dinar', 'JD');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KZT', 'Kazakhstani Tenge', 'лв');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KES', 'Kenyan Shilling', 'KSh');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KWD', 'Kuwaiti Dinar', 'KD');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KGS', 'Kyrgystani Som', 'лв');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LAK', 'Laotian Kip', '₭');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LVL', 'Latvian Lats', 'Ls');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LBP', 'Lebanese Pound', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LSL', 'Lesotho Loti', 'M');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LRD', 'Liberian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LYD', 'Libyan Dinar', 'LD');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LTL', 'Lithuanian Litas', 'Lt');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MOP', 'Macanese Pataca', 'MOP$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MKD', 'Macedonian Denar', 'ден');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MGA', 'Malagasy Ariary', 'Ar');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MWK', 'Malawian Kwacha', 'MK');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MYR', 'Malaysian Ringgit', 'RM');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MVR', 'Maldivian Rufiyaa', 'Rf');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MRO', 'Mauritanian Ouguiya', 'UM');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MUR', 'Mauritian Rupee', '₨');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MXN', 'Mexican Peso', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MDL', 'Moldovan Leu', 'lei');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MNT', 'Mongolian Tugrik', '₮');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MAD', 'Moroccan Dirham', 'MAD');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MZN', 'Mozambican Metical', 'MT');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('MMK', 'Myanmar Kyat', 'K');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('NAD', 'Namibian Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('NPR', 'Nepalese Rupee', '₨');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ANG', 'Netherlands Antillean Guilder', 'ƒ');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TWD', 'New Taiwan Dollar', 'NT$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('NZD', 'New Zealand Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('NIO', 'Nicaraguan Córdoba', 'C$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('NGN', 'Nigerian Naira', '₦');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KPW', 'North Korean Won', '₩');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('NOK', 'Norwegian Krone', 'kr');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('OMR', 'Omani Rial', '﷼');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PKR', 'Pakistani Rupee', '₨');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PAB', 'Panamanian Balboa', 'B/.');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PGK', 'Papua New Guinean Kina', 'K');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PYG', 'Paraguayan Guarani', 'Gs');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PEN', 'Peruvian Nuevo Sol', 'S/.');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PHP', 'Philippine Peso', '₱');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('PLN', 'Polish Zloty', 'zł');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('QAR', 'Qatari Rial', '﷼');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('RON', 'Romanian Leu', 'lei');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('RUB', 'Russian Ruble', '₽');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('RWF', 'Rwandan Franc', 'R₣');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SHP', 'Saint Helena Pound', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SVC', 'Salvadoran Colón', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('WST', 'Samoan Tala', 'WS$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('STD', 'São Tomé and Príncipe Dobra', 'Db');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SAR', 'Saudi Riyal', '﷼');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('RSD', 'Serbian Dinar', 'Дин.');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SCR', 'Seychellois Rupee', '₨');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SLL', 'Sierra Leonean Leone', 'Le');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SGD', 'Singapore Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SBD', 'Solomon Islands Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SOS', 'Somali Shilling', 'S');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('ZAR', 'South African Rand', 'R');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('KRW', 'South Korean Won', '₩');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('LKR', 'Sri Lankan Rupee', '₨');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SDG', 'Sudanese Pound', 'ج.س.');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SRD', 'Surinamese Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SZL', 'Swazi Lilangeni', 'E');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SEK', 'Swedish Krona', 'kr');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('CHF', 'Swiss Franc', 'CHF');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('SYP', 'Syrian Pound', '£');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TJS', 'Tajikistani Somoni', 'SM');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TZS', 'Tanzanian Shilling', 'TSh');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('THB', 'Thai Baht', '฿');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TOP', 'Tongan Pa`anga', 'T$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TTD', 'Trinidad and Tobago Dollar', 'TT$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TND', 'Tunisian Dinar', 'د.ت');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TRY', 'Turkish Lira', '₺');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('TMT', 'Turkmenistani Manat', 'T');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('UGX', 'Ugandan Shilling', 'USh');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('UAH', 'Ukrainian Hryvnia', '₴');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('UYU', 'Uruguayan Peso', '$U');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('USD', 'US Dollar', '$');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('UZS', 'Uzbekistan Som', 'лв');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('VUV', 'Vanuatu Vatu', 'VT');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('VEF', 'Venezuelan Bolívar', 'Bs');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('VND', 'Vietnamese Dong', '₫');INSERT INTO currency (currencyId, currencyName, currencySymbol) VALUES ('YER', 'Yemeni Rial', '﷼');