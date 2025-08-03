// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

import {Arrays} from "@openzeppelin/contracts/utils/Arrays.sol";
import {ERC1155} from "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";
import {IERC20} from "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import {Ownable} from "@openzeppelin/contracts/access/Ownable.sol";

contract Coincento is ERC1155, Ownable {
    using Arrays for uint256[];

    uint private constant ABSENT = 0;
    uint private constant ACTIVE = 1;
    uint private constant SUSPENDED = 2;

    mapping(uint256 id => uint) private _allowedIds;

    // TODO set uri
    constructor(address initialOwner) ERC1155("") Ownable(initialOwner) {}

    // TODO fire events for account registration/suspension/activation

    function registerToken(uint256 id) public onlyOwner {
        require(id <= type(uint160).max, "Invalid id");
        require(_allowedIds[id] == ABSENT, "Alread registered");
        _allowedIds[id] = ACTIVE;
    }

    function suspendToken(uint256 id) public onlyOwner {
        require(_allowedIds[id] != ABSENT, "Not registered");
        _allowedIds[id] = SUSPENDED;
    }

    function resumeToken(uint256 id) public onlyOwner {
        require(_allowedIds[id] != ABSENT, "Not registered");
        _allowedIds[id] = ACTIVE;
    }

    function deposit(address from, uint256 id, uint256 value, bytes memory data) public {
        _requireActive(id);
        // TODO Fix it
        // IERC20 token = IERC20(address(uint160(id)));
        // token.transferFrom(from, address(this), value);
        _mint(from, id, value, data);
    }

    function withdraw(address to, uint256 id, uint256 value, bytes memory data) public {
        _requireActive(id);
        IERC20 token = IERC20(address(uint160(id)));
        token.transfer(to, value);
        _burn(to, id, value);
    }

    /// @inheritdoc ERC1155
    function safeTransferFrom(address from, address to, uint256 id, uint256 value, bytes memory data) public override {
        _requireActive(id);
        super.safeTransferFrom(from, to, id, value, data);
    }

    /// @inheritdoc ERC1155
    function safeBatchTransferFrom(
        address from,
        address to,
        uint256[] memory ids,
        uint256[] memory values,
        bytes memory data
    ) public override {
        for (uint256 i = 0; i < ids.length; ++i) {
            _requireActive(ids.unsafeMemoryAccess(i));
        }
        super.safeBatchTransferFrom(from, to, ids, values, data);
    }

    function _requireActive(uint256 id) private view {
        require(_allowedIds[id] == ACTIVE, "Token is not active");
    }
}