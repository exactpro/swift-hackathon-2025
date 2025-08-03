// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract UsdtToken is ERC20 {
    constructor() ERC20("Fake USDT Token", "USDT") {
        _mint(msg.sender, 1000000 * 10 ** decimals());
    }
}
