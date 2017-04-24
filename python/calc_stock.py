# encoding:utf8
"""
@desc: 
@version: 1.0
@author: huangqingwei(huangqingwei@baidu.com)
@license: Copyright (c) 2016 Baidu.com,Inc. All Rights Reserved
@software: PyCharm Community Edition
@file: calc_stock.py
@time: 2017/4/18 17:24
"""

# 下面是按25股行权策略，股价172.61$; 字段依次为行权数, 剩余股票数, 总额/税, 零头美金;

# 1, 0, 172.61/5.18, 137.41; (1*25)remain: 0ADS + 3435.25$ == 18ADS + 328.27$

# 3, 2, 517.83/36.49, 106.09; (3*8+1)remain: 16ADS + 986$ == 18ADS + 640$; (3*7+4)remain: 17ADS + 827.96$ == 18ADS + 655.35

# 4, 3, 690.44/57.25, 85.33; (4*6+1)remain: 18ADS + 684$; (4*5+5)remain: 19ADS + 477.45$ == 18ADS + 650$

# 5, 4, 863.05/91.77, 50.8; (5*5)remain: 20ADS + 254$ == 18ADS + 599$

# 6, 5, 1035.66/126.29, 6.28; (6*4+1)remain: 20ADS + 162.53$ == 18ADS + 507.75

# 7, 5, 1208.27/160.81, 154.36; (7*3+4)remain: 18ADS + 548.41$

# 8,

# 10, 7, 1726.10/285.4, 202.62; remain: 18ADS + 456.04$

# 25, 18, 4315.25/932.42, 245.69; remain: 18ADS + 245.69$

dollor_to_rmb = 6.8835
stock_price_dollor = 172.61


def calc_fax(money_dollar):
    """
    计算个人所得税函数; 入参-dollar, 返回-dollar
    :param money_dollar:
    :return:
    """
    money_rmb = money_dollar * dollor_to_rmb
    if money_rmb <= 1500:
        fax_rmb = money_rmb * 0.03
    elif money_rmb <= 4500:
        fax_rmb = money_rmb * 0.1 - 105
    elif money_rmb <= 9000:
        fax_rmb = money_rmb * 0.2 - 555
    elif money_rmb <= 35000:
        fax_rmb = money_rmb * 0.25 - 1005
    elif money_rmb <= 55000:
        fax_rmb = money_rmb * 0.3 - 2755
    elif money_rmb <= 80000:
        fax_rmb = money_rmb * 0.35 - 5505
    else:
        fax_rmb = money_rmb * 0.45 - 13505
    return fax_rmb / dollor_to_rmb


for i in range(1, 25):
    money_dollar = i * stock_price_dollor
    print i, money_dollar, calc_fax(money_dollar)
