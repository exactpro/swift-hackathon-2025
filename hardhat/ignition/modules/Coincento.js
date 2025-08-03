const { buildModule } = require("@nomicfoundation/hardhat-ignition/modules");
const { parseUnits } = require("ethers");
const TokensModule = require("./FakeTokens");

const CoincentoModule = buildModule("CoincentoModule", m => {
  function registerToken(token) {
    return m.call(coincento, "registerToken", [token], { id: `register_${token.contractName}` });
  }

  function depositToken(token, to, amount, registerTokenFuture) {
    const id = `${token.contractName}_${to.accountIndex}`;

    const transferFuture = m.call(token, "transfer", [to, amount], { id: `transfer_${id}` });
    const approveFuture = m.call(token, "approve", [coincento, amount], { id: `approve_${id}` })
    m.call(
      coincento, "deposit", [to, token, amount, "0x00"],
      { from: bankA, after: [registerTokenFuture, transferFuture, approveFuture], id: `deposit_${id}` }
    )
  }

  const deployer = m.getAccount(0);
  const bankA = m.getAccount(1);
  const bankB = m.getAccount(2);

  const oneThousand = parseUnits("1000", 18);

  const coincento = m.contract("Coincento", [deployer]);
  const tokens = Object.values(m.useModule(TokensModule));

  const registerTokenFutures = tokens.map(token => registerToken(token));
  tokens.map((token, index) => depositToken(token, bankA, oneThousand, registerTokenFutures[index]));
  tokens.map((token, index) => depositToken(token, bankB, oneThousand, registerTokenFutures[index]));

  return { coincento };
});

module.exports = CoincentoModule;