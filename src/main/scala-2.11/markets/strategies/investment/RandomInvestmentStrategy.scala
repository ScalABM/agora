package markets.strategies.investment

import markets.orders.Order


trait RandomInvestmentStrategy[T <: Order] extends InvestmentStrategy[T]
  with RandomTradable[T]

