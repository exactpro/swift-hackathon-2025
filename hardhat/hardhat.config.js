require("@nomicfoundation/hardhat-toolbox");
const { Wallet } = require("ethers")

const BESU_URL = process.env.BESU_URL || "http://localhost:8545/";
const SALT_SEED = "6ec835c38b957f775cc9a8"
const DEPLOYER_PRIVATE_KEY = "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";
const BANK_A_PRIVATE_KEY = "ae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f";
const BANK_B_PRIVATE_KEY = "f3034bfc9d9a52003e73a7bcd109ac00d3cf823c806c3c7c77e479c5e79c5f63";
const deployerAddress = new Wallet(DEPLOYER_PRIVATE_KEY).address

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: "0.8.28",
  networks: {
    besu: {
      url: BESU_URL,
      chainId: 1337,
      accounts: [DEPLOYER_PRIVATE_KEY, BANK_A_PRIVATE_KEY, BANK_B_PRIVATE_KEY],
    },
  },
  ignition: {
    strategyConfig: {
      create2: {
        // To learn more about salts, see the CreateX documentation
        salt: deployerAddress + "00" + SALT_SEED,
      },
    },
  },
  namedAccounts: {
    deployer: {
      default: 0
    },
    bankA: {
      default: 1
    },
    bankB: {
      default: 2
    },
  },
};
