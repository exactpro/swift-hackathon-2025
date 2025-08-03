const { buildModule } = require("@nomicfoundation/hardhat-ignition/modules");

const UsdcTokenModule = buildModule("FakeTokensModule", (m) => {
  const eurcToken = m.contract("EurcToken");
  const usdcToken = m.contract("UsdcToken");
  const usdtToken = m.contract("UsdtToken");

  return { eurcToken, usdcToken, usdtToken };
});

module.exports = UsdcTokenModule;
