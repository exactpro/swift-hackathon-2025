<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <article class="prose dark:prose-invert max-w-4xl mx-auto p-6">
    <h1>Exactpro DLT-based Platform Sync-USDC</h1>

    <p>
      <a href="https://exactpro.com/" target="_blank" rel="noopener noreferrer">Exactpro</a>
      has been building testing and development expertise in the DLT/ Blockchain space since 2018. Since then, we have
      implemented multiple regulatory-grade testing and development projects with leading technology providers and
      digital exchanges disrupting traditional finance.
    </p>

    <p>
      The current solution is a DLT-based platform <strong>Sync-USDC</strong>. While addressing the required challenge
      objectives – real-time accuracy, transparency, interoperability and operational efficiency – the proposed solution
      enables seamless integration with the banking infrastructure based on ISO 20022, capitalising on and requiring
      minimal modifications to the existing banking platforms.
    </p>

    <p>
      The main components of the platform are: Bank's App, Core Banking Application (see links in the main Root
      Navigation panel) and the EVM blockchain with customised smart contracts. This allows clients and banking
      operators to follow their workflows, including management of the transactions and their reporting. The smart
      contract (customised ERC-1155 smart token) will allow the creation and execution of smart contracts within the
      Ethereum Mainnet Network.
    </p>

    <p>
      The solution is entity-, bank-, and technology-agnostic, it is integrated with the transactions dashboard for
      further analysis, operational flow management and analytics. It enables transaction visibility across participants
      and end-to-end traceability for the use case flow and further process mapping, reconciliation, streamlined
      internal audits and regulatory reporting.
    </p>

    <h2>Application Navigation</h2>

    <h3>Banking Applications</h3>
    <ul>
      <li>
        <strong>Client Banking Interfaces:</strong>
        <ul>
          <li><RouterLink to="/banka">Bank A - Client Dashboard</RouterLink></li>
          <li><RouterLink to="/bankb">Bank B - Client Dashboard</RouterLink></li>
          <li><RouterLink to="/banka/exchange">Bank A - Exchange</RouterLink></li>
          <li><RouterLink to="/bankb/exchange">Bank B - Exchange</RouterLink></li>
        </ul>
      </li>
    </ul>

    <h3>Core Banking & Administration</h3>
    <ul>
      <li>
        <strong>Administrative Interfaces:</strong>
        <ul>
          <li>
            Admin Dashboard:
            <ul>
              <li><RouterLink to="/banka/admin">Bank A - Admin Dashboard</RouterLink></li>
              <li><RouterLink to="/bankb/admin">Bank B - Admin Dashboard</RouterLink></li>
            </ul>
          </li>
        </ul>
      </li>
    </ul>

    <h3>Transaction Monitoring</h3>
    <ul>
      <li>
        <strong>Transaction Views:</strong>
        <ul>
          <li>
            Transaction Status Tracking:
            <ul>
              <li>Bank A - Individual transaction status pages (/banka/admin/transfers/:uetr)</li>
              <li>Bank B - Individual transaction status pages (/bankb/admin/transfers/:uetr)</li>
            </ul>
          </li>
          <li>Transactions Dashboard (available in admin interfaces above)</li>
          <li>Cross-bank Transaction Visibility (Both Bank A & B)</li>
          <li>Audit Trail & Compliance Reporting (Both Bank A & B)</li>
        </ul>
      </li>
    </ul>

    <h2>Additional Monitoring Solutions</h2>
    <ul>
      <li>
        <strong>Infrastructure Monitoring:</strong>
        <ul>
          <li>Kafka: <em>linkTBD</em></li>
          <li>Block Explorer: <em>linkTBD</em></li>
        </ul>
      </li>
    </ul>

    <h2>Key Features</h2>
    <ul>
      <li><strong>Real-time Accuracy:</strong> Instant transaction processing and validation</li>
      <li><strong>Transparency:</strong> Full visibility across all participants</li>
      <li><strong>Interoperability:</strong> Seamless integration with existing banking systems</li>
      <li><strong>Operational Efficiency:</strong> Streamlined workflows and automated processes</li>
      <li><strong>ISO 20022 Compliance:</strong> Standards-based messaging and data formats</li>
      <li><strong>Smart Contract Integration:</strong> Customised ERC-1155 token implementation</li>
    </ul>

    <p>
      <small>
        <em>
          This platform demonstrates the integration of traditional banking infrastructure with modern DLT technology,
          providing a regulatory-compliant solution for real-time cross-border payments and settlements.
        </em>
      </small>
    </p>
  </article>
</template>
